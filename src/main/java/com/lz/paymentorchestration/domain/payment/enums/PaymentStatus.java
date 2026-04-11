package com.lz.paymentorchestration.domain.payment.enums;

public enum PaymentStatus {
    CREATED,
    PENDING_PROVIDER,
    AUTHORIZED,
    PAID,
    FAILED,
    CANCELLED,
    MANUAL_REVIEW;

    public boolean isFinalStatus() {
        return this == PAID || this == FAILED || this == CANCELLED;
    }

    public boolean canTransitionTo(PaymentStatus target) {
        if (target == null) {
            return false;
        }

        if (this == target) {
            return true;
        }

        return switch (this) {
            case CREATED -> target == PENDING_PROVIDER
                    || target == MANUAL_REVIEW
                    || target == FAILED
                    || target == CANCELLED;

            case PENDING_PROVIDER -> target == AUTHORIZED
                    || target == PAID
                    || target == FAILED
                    || target == CANCELLED;

            case AUTHORIZED -> target == PAID
                    || target == FAILED
                    || target == CANCELLED;

            case MANUAL_REVIEW -> target == PENDING_PROVIDER
                    || target == FAILED
                    || target == CANCELLED;

            case PAID, FAILED, CANCELLED -> false;
        };
    }
}