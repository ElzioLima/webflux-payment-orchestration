package com.lz.paymentorchestration.entrypoint.http.handler;

import com.lz.paymentorchestration.application.usecase.HandlePaymentWebhookUseCase;
import com.lz.paymentorchestration.entrypoint.http.request.ProviderWebhookRequest;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

public class PaymentWebhookHandler {

    private final HandlePaymentWebhookUseCase handlePaymentWebhookUseCase;

    public PaymentWebhookHandler(HandlePaymentWebhookUseCase handlePaymentWebhookUseCase) {
        this.handlePaymentWebhookUseCase = handlePaymentWebhookUseCase;
    }

    public Mono<ServerResponse> handleProviderWebhook(ServerRequest request) {
        return request.bodyToMono(ProviderWebhookRequest.class)
                .flatMap(webhook -> handlePaymentWebhookUseCase.execute(webhook)
                        .thenReturn(webhook))
                .flatMap(webhook -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(Map.of(
                                "status", "processed",
                                "providerPaymentId", webhook.providerPaymentId())));
    }
}