package com.lz.paymentorchestration.application.usecase;

import com.lz.paymentorchestration.entrypoint.http.request.ProviderWebhookRequest;
import reactor.core.publisher.Mono;

public interface HandlePaymentWebhookUseCase {
    Mono<Void> execute(ProviderWebhookRequest request);
}