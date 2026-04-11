package com.lz.paymentorchestration.entrypoint.http.request;

public record ProviderWebhookRequest(
        String providerPaymentId,
        String status,
        String externalReference
) {
}