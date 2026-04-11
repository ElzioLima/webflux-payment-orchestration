package com.lz.paymentorchestration.application.service;

import com.lz.paymentorchestration.domain.payment.enums.PaymentStatus;

public class ProviderStatusMapper {

    public PaymentStatus mapCreationStatus(String providerStatus) {
        if (providerStatus == null || providerStatus.isBlank()) {
            return PaymentStatus.PENDING_PROVIDER;
        }

        return switch (providerStatus.toLowerCase()) {
            case "approved", "paid" -> PaymentStatus.PAID;
            case "authorized", "authorised" -> PaymentStatus.AUTHORIZED;
            case "in_process", "in-process", "processing", "pending", "created" -> PaymentStatus.PENDING_PROVIDER;
            case "cancelled", "canceled" -> PaymentStatus.CANCELLED;
            case "rejected", "failed", "denied" -> PaymentStatus.FAILED;
            default -> PaymentStatus.PENDING_PROVIDER;
        };
    }
}