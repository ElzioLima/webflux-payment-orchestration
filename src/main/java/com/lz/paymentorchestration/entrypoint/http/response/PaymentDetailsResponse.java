package com.lz.paymentorchestration.entrypoint.http.response;

import com.lz.paymentorchestration.domain.payment.enums.PaymentMethod;
import com.lz.paymentorchestration.domain.payment.enums.PaymentProvider;
import com.lz.paymentorchestration.domain.payment.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PaymentDetailsResponse(
                String paymentId,
                String orderId,
                PaymentMethod paymentMethod,
                PaymentProvider provider,
                String providerPaymentId,
                BigDecimal amount,
                String currency,
                PaymentStatus status,
                OffsetDateTime createdAt,
                OffsetDateTime updatedAt) {
}