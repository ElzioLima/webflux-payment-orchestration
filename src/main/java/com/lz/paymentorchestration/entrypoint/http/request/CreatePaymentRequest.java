package com.lz.paymentorchestration.entrypoint.http.request;

import com.lz.paymentorchestration.domain.payment.enums.PaymentMethod;

import java.math.BigDecimal;

public record CreatePaymentRequest(
        String orderId,
        PaymentMethod paymentMethod,
        BigDecimal amount,
        String currency,
        String description) {
}