package com.lz.paymentorchestration.application.usecase;

import com.lz.paymentorchestration.entrypoint.http.request.CreatePaymentRequest;
import com.lz.paymentorchestration.entrypoint.http.response.CreatePaymentResponse;
import reactor.core.publisher.Mono;

public interface CreatePaymentUseCase {
    Mono<CreatePaymentResponse> execute(CreatePaymentRequest request);
}