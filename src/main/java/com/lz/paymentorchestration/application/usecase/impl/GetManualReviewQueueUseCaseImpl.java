package com.lz.paymentorchestration.application.usecase.impl;

import com.lz.paymentorchestration.application.service.model.ManualReviewCustomerData;
import com.lz.paymentorchestration.application.usecase.GetManualReviewQueueUseCase;
import com.lz.paymentorchestration.domain.payment.Payment;
import com.lz.paymentorchestration.domain.payment.enums.PaymentStatus;
import com.lz.paymentorchestration.domain.payment.policy.ManualReviewQueuePolicy;
import com.lz.paymentorchestration.entrypoint.http.response.ManualReviewQueueItemResponse;
import com.lz.paymentorchestration.infrastructure.config.ManualReviewQueueProperties;
import com.lz.paymentorchestration.ports.out.CustomerReviewDataGateway;
import com.lz.paymentorchestration.ports.out.PaymentRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

public class GetManualReviewQueueUseCaseImpl implements GetManualReviewQueueUseCase {

    private final PaymentRepository paymentRepository;
    private final CustomerReviewDataGateway customerReviewDataGateway;
    private final ManualReviewQueuePolicy manualReviewQueuePolicy;
    private final ManualReviewQueueProperties queueProperties;

    public GetManualReviewQueueUseCaseImpl(
            PaymentRepository paymentRepository,
            CustomerReviewDataGateway customerReviewDataGateway,
            ManualReviewQueuePolicy manualReviewQueuePolicy,
            ManualReviewQueueProperties queueProperties) {
        this.paymentRepository = paymentRepository;
        this.customerReviewDataGateway = customerReviewDataGateway;
        this.manualReviewQueuePolicy = manualReviewQueuePolicy;
        this.queueProperties = queueProperties;
    }

    @Override
    public Flux<ManualReviewQueueItemResponse> execute() {
        return paymentRepository.findByStatus(PaymentStatus.MANUAL_REVIEW)
                .take(queueProperties.getBatchSize())
                .flatMap(
                        payment -> Mono
                                .fromCallable(() -> customerReviewDataGateway.findByOrderId(payment.getOrderId()))
                                .subscribeOn(Schedulers.boundedElastic())
                                .timeout(Duration.ofSeconds(queueProperties.getEnrichmentTimeoutSeconds()))
                                .filter(customerData -> manualReviewQueuePolicy.isActionable(payment, customerData))
                                .map(customerData -> toQueueItem(payment, customerData))
                                .onErrorResume(ex -> Mono.empty()),
                        queueProperties.getEnrichmentConcurrency());
    }

    private ManualReviewQueueItemResponse toQueueItem(
            Payment payment,
            ManualReviewCustomerData customerData) {
        return new ManualReviewQueueItemResponse(
                payment.getId().value(),
                payment.getOrderId(),
                payment.getPaymentMethod(),
                payment.getProvider(),
                payment.getAmount().amount(),
                payment.getAmount().currency().getCurrencyCode(),
                manualReviewQueuePolicy.resolveCategory(payment, customerData),
                true,
                customerData.customerSegment(),
                customerData.customerBlocked(),
                customerData.previousManualReviewCount(),
                payment.getCreatedAt(),
                payment.getUpdatedAt());
    }
}