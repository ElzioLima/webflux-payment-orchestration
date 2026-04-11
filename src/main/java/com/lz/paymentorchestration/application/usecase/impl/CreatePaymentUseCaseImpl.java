package com.lz.paymentorchestration.application.usecase.impl;

import com.lz.paymentorchestration.application.service.PaymentPrecheckService;
import com.lz.paymentorchestration.application.service.ProviderStatusMapper;
import com.lz.paymentorchestration.application.service.model.PaymentPrecheckResult;
import com.lz.paymentorchestration.application.usecase.CreatePaymentUseCase;
import com.lz.paymentorchestration.domain.payment.Order;
import com.lz.paymentorchestration.domain.payment.Payment;
import com.lz.paymentorchestration.domain.payment.enums.PaymentDecision;
import com.lz.paymentorchestration.domain.payment.enums.PaymentProvider;
import com.lz.paymentorchestration.domain.payment.enums.PaymentStatus;
import com.lz.paymentorchestration.domain.payment.vo.Money;
import com.lz.paymentorchestration.domain.payment.vo.ProviderPaymentId;
import com.lz.paymentorchestration.entrypoint.http.error.ApiBusinessException;
import com.lz.paymentorchestration.entrypoint.http.error.ApiErrorCode;
import com.lz.paymentorchestration.entrypoint.http.request.CreatePaymentRequest;
import com.lz.paymentorchestration.entrypoint.http.response.CreatePaymentResponse;
import com.lz.paymentorchestration.ports.out.OrderRepository;
import com.lz.paymentorchestration.ports.out.PaymentProviderGateway;
import com.lz.paymentorchestration.ports.out.PaymentRepository;
import com.lz.paymentorchestration.ports.out.model.ProviderCreatePaymentCommand;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

public class CreatePaymentUseCaseImpl implements CreatePaymentUseCase {

        private final OrderRepository orderRepository;
        private final PaymentRepository paymentRepository;
        private final PaymentProviderGateway paymentProviderGateway;
        private final ProviderStatusMapper providerStatusMapper;
        private final PaymentPrecheckService paymentPrecheckService;

        public CreatePaymentUseCaseImpl(
                        OrderRepository orderRepository,
                        PaymentRepository paymentRepository,
                        PaymentProviderGateway paymentProviderGateway,
                        ProviderStatusMapper providerStatusMapper,
                        PaymentPrecheckService paymentPrecheckService) {
                this.orderRepository = orderRepository;
                this.paymentRepository = paymentRepository;
                this.paymentProviderGateway = paymentProviderGateway;
                this.providerStatusMapper = providerStatusMapper;
                this.paymentPrecheckService = paymentPrecheckService;
        }

        @Override
        public Mono<CreatePaymentResponse> execute(CreatePaymentRequest request) {
                Money requestedAmount = Money.of(request.amount(), request.currency());

                return paymentRepository.findByOrderIdAndPaymentMethod(
                                request.orderId(),
                                request.paymentMethod().name())
                                .map(this::toResponse)
                                .switchIfEmpty(
                                                orderRepository.findById(request.orderId())
                                                                .switchIfEmpty(Mono.error(new ApiBusinessException(
                                                                                ApiErrorCode.ORDER_NOT_FOUND,
                                                                                "No order was found for the provided orderId.",
                                                                                HttpStatus.NOT_FOUND)))
                                                                .flatMap(order -> processNewPayment(order, request,
                                                                                requestedAmount)));
        }

        private Mono<CreatePaymentResponse> processNewPayment(
                        Order order,
                        CreatePaymentRequest request,
                        Money requestedAmount) {
                PaymentPrecheckResult precheckResult = paymentPrecheckService.evaluate(order, requestedAmount);

                if (precheckResult.decision() == PaymentDecision.REJECTED) {
                        return Mono.error(mapPrecheckRejection(precheckResult.reason()));
                }

                OffsetDateTime now = OffsetDateTime.now();

                Payment payment = Payment.newPayment(
                                order.getId(),
                                request.paymentMethod(),
                                PaymentProvider.MERCADO_PAGO,
                                requestedAmount,
                                now);

                if (precheckResult.decision() == PaymentDecision.MANUAL_REVIEW) {
                        payment.markManualReview(now);

                        return paymentRepository.save(payment)
                                        .map(this::toResponse);
                }

                return sendToProvider(payment, request)
                                .flatMap(paymentRepository::save)
                                .map(this::toResponse);
        }

        private Mono<Payment> sendToProvider(Payment payment, CreatePaymentRequest request) {
                ProviderCreatePaymentCommand command = new ProviderCreatePaymentCommand(
                                payment.getId().value(),
                                payment.getAmount(),
                                request.description(),
                                payment.getPaymentMethod());

                return paymentProviderGateway.createPayment(command)
                                .map(result -> {
                                        PaymentStatus mappedStatus = providerStatusMapper
                                                        .mapCreationStatus(result.providerStatus());

                                        payment.confirmProviderSubmission(
                                                        new ProviderPaymentId(result.providerPaymentId()),
                                                        mappedStatus,
                                                        OffsetDateTime.now());

                                        return payment;
                                });
        }

        private CreatePaymentResponse toResponse(Payment payment) {
                return new CreatePaymentResponse(
                                payment.getId().value(),
                                payment.getStatus(),
                                payment.getProvider(),
                                payment.getProviderPaymentId() != null ? payment.getProviderPaymentId().value() : null,
                                payment.getCreatedAt());
        }

        private ApiBusinessException mapPrecheckRejection(String reason) {
                return switch (reason) {
                        case "Payment amount must be positive" -> new ApiBusinessException(
                                        ApiErrorCode.INVALID_PAYMENT_AMOUNT,
                                        "The payment amount must be greater than zero.",
                                        HttpStatus.valueOf(422));
                        case "Order is not pending payment" -> new ApiBusinessException(
                                        ApiErrorCode.ORDER_NOT_PENDING_PAYMENT,
                                        "The order is not in a payable state.",
                                        HttpStatus.valueOf(422));
                        case "Customer is blocked" -> new ApiBusinessException(
                                        ApiErrorCode.CUSTOMER_BLOCKED,
                                        "The customer is blocked for payment operations.",
                                        HttpStatus.valueOf(422));
                        case "Payment amount does not match order total" -> new ApiBusinessException(
                                        ApiErrorCode.PAYMENT_AMOUNT_MISMATCH,
                                        "The informed amount does not match the order total.",
                                        HttpStatus.valueOf(422));
                        case "Payment amount exceeds maximum allowed limit" -> new ApiBusinessException(
                                        ApiErrorCode.PAYMENT_AMOUNT_EXCEEDS_LIMIT,
                                        "The informed amount exceeds the maximum allowed payment limit.",
                                        HttpStatus.valueOf(422));
                        default -> new ApiBusinessException(
                                        ApiErrorCode.INTERNAL_ERROR,
                                        reason,
                                        HttpStatus.INTERNAL_SERVER_ERROR);
                };
        }
}