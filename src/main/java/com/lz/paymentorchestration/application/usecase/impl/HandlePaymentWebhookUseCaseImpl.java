package com.lz.paymentorchestration.application.usecase.impl;

import com.lz.paymentorchestration.application.service.ProviderStatusMapper;
import com.lz.paymentorchestration.application.usecase.HandlePaymentWebhookUseCase;
import com.lz.paymentorchestration.domain.payment.Payment;
import com.lz.paymentorchestration.domain.payment.enums.PaymentStatus;
import com.lz.paymentorchestration.entrypoint.http.error.ApiBusinessException;
import com.lz.paymentorchestration.entrypoint.http.error.ApiErrorCode;
import com.lz.paymentorchestration.entrypoint.http.request.ProviderWebhookRequest;
import com.lz.paymentorchestration.ports.out.AuditRepository;
import com.lz.paymentorchestration.ports.out.PaymentRepository;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

public class HandlePaymentWebhookUseCaseImpl implements HandlePaymentWebhookUseCase {

    private final PaymentRepository paymentRepository;
    private final AuditRepository auditRepository;
    private final ProviderStatusMapper providerStatusMapper;

    public HandlePaymentWebhookUseCaseImpl(
            PaymentRepository paymentRepository,
            AuditRepository auditRepository,
            ProviderStatusMapper providerStatusMapper) {
        this.paymentRepository = paymentRepository;
        this.auditRepository = auditRepository;
        this.providerStatusMapper = providerStatusMapper;
    }

    @Override
    public Mono<Void> execute(ProviderWebhookRequest request) {
        return paymentRepository.findByProviderPaymentId(request.providerPaymentId())
                .switchIfEmpty(Mono.error(new ApiBusinessException(
                        ApiErrorCode.PAYMENT_NOT_FOUND_BY_PROVIDER,
                        "No internal payment was found for the provided providerPaymentId.",
                        HttpStatus.NOT_FOUND)))
                .flatMap(payment -> updatePaymentFromWebhook(payment, request))
                .flatMap(paymentRepository::save)
                .flatMap(payment -> auditRepository.register(
                        "PAYMENT_PROVIDER_WEBHOOK_PROCESSED",
                        buildAuditPayload(payment, request)));
    }

    private Mono<Payment> updatePaymentFromWebhook(Payment payment, ProviderWebhookRequest request) {
        PaymentStatus targetStatus = providerStatusMapper.mapCreationStatus(request.status());

        if (payment.isFinalStatus()) {
            return Mono.just(payment);
        }

        try {
            payment.moveTo(targetStatus, OffsetDateTime.now());
            return Mono.just(payment);
        } catch (IllegalStateException ex) {
            return Mono.error(new ApiBusinessException(
                    ApiErrorCode.INVALID_PAYMENT_STATUS_TRANSITION,
                    "The webhook status transition was rejected by the payment state machine.",
                    HttpStatus.valueOf(422)));
        }
    }

    private String buildAuditPayload(Payment payment, ProviderWebhookRequest request) {
        return """
                paymentId=%s,providerPaymentId=%s,externalReference=%s,externalStatus=%s,internalStatus=%s
                """.formatted(
                payment.getId().value(),
                request.providerPaymentId(),
                request.externalReference(),
                request.status(),
                payment.getStatus());
    }
}