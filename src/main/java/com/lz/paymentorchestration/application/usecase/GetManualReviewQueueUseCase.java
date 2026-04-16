package com.lz.paymentorchestration.application.usecase;

import com.lz.paymentorchestration.entrypoint.http.response.ManualReviewQueueItemResponse;
import reactor.core.publisher.Flux;

public interface GetManualReviewQueueUseCase {
    Flux<ManualReviewQueueItemResponse> execute();
}