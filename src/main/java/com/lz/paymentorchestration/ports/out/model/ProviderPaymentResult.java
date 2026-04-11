package com.lz.paymentorchestration.ports.out.model;

public record ProviderPaymentResult(
        String providerPaymentId,
        String providerStatus) {
}