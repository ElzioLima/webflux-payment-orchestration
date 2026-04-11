package com.lz.paymentorchestration.application.usecase;

import com.lz.paymentorchestration.entrypoint.http.response.PaymentDetailsResponse;
import reactor.core.publisher.Mono;

public interface GetPaymentUseCase {
    Mono<PaymentDetailsResponse> execute(String paymentId);
}