package com.lz.paymentorchestration.application.service.model;

import com.lz.paymentorchestration.domain.payment.enums.PaymentDecision;

public record PaymentPrecheckResult(
        PaymentDecision decision,
        String reason) {
    public static PaymentPrecheckResult approved() {
        return new PaymentPrecheckResult(PaymentDecision.APPROVED, "Payment approved for provider submission");
    }

    public static PaymentPrecheckResult rejected(String reason) {
        return new PaymentPrecheckResult(PaymentDecision.REJECTED, reason);
    }

    public static PaymentPrecheckResult manualReview(String reason) {
        return new PaymentPrecheckResult(PaymentDecision.MANUAL_REVIEW, reason);
    }
}