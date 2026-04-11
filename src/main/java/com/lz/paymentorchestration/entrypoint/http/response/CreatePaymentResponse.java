package com.lz.paymentorchestration.entrypoint.http.response;

import com.lz.paymentorchestration.domain.payment.enums.PaymentProvider;
import com.lz.paymentorchestration.domain.payment.enums.PaymentStatus;

import java.time.OffsetDateTime;

public record CreatePaymentResponse(
        String paymentId,
        PaymentStatus status,
        PaymentProvider provider,
        String providerPaymentId,
        OffsetDateTime createdAt) {
}