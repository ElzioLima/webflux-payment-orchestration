package com.lz.paymentorchestration.application.usecase.impl;

import com.lz.paymentorchestration.application.usecase.GetPaymentUseCase;
import com.lz.paymentorchestration.domain.payment.Payment;
import com.lz.paymentorchestration.entrypoint.http.error.ApiBusinessException;
import com.lz.paymentorchestration.entrypoint.http.error.ApiErrorCode;
import com.lz.paymentorchestration.entrypoint.http.response.PaymentDetailsResponse;
import com.lz.paymentorchestration.ports.out.PaymentRepository;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

public class GetPaymentUseCaseImpl implements GetPaymentUseCase {

    private final PaymentRepository paymentRepository;

    public GetPaymentUseCaseImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public Mono<PaymentDetailsResponse> execute(String paymentId) {
        return paymentRepository.findById(paymentId)
                .switchIfEmpty(Mono.error(new ApiBusinessException(
                        ApiErrorCode.PAYMENT_NOT_FOUND,
                        "No payment was found for the provided paymentId.",
                        HttpStatus.NOT_FOUND)))
                .map(this::toResponse);
    }

    private PaymentDetailsResponse toResponse(Payment payment) {
        return new PaymentDetailsResponse(
                payment.getId().value(),
                payment.getOrderId(),
                payment.getPaymentMethod(),
                payment.getProvider(),
                payment.getProviderPaymentId() != null ? payment.getProviderPaymentId().value() : null,
                payment.getAmount().amount(),
                payment.getAmount().currency().getCurrencyCode(),
                payment.getStatus(),
                payment.getCreatedAt(),
                payment.getUpdatedAt());
    }
}