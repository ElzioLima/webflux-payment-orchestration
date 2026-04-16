package com.lz.paymentorchestration.entrypoint.http.handler;

import com.lz.paymentorchestration.application.usecase.CreatePaymentUseCase;
import com.lz.paymentorchestration.application.usecase.GetManualReviewQueueUseCase;
import com.lz.paymentorchestration.application.usecase.GetPaymentUseCase;
import com.lz.paymentorchestration.entrypoint.http.request.CreatePaymentRequest;
import com.lz.paymentorchestration.entrypoint.http.response.ManualReviewQueueItemResponse;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public class PaymentHandler {

    private final CreatePaymentUseCase createPaymentUseCase;
    private final GetPaymentUseCase getPaymentUseCase;
    private final GetManualReviewQueueUseCase getManualReviewQueueUseCase;

    public PaymentHandler(
            CreatePaymentUseCase createPaymentUseCase,
            GetPaymentUseCase getPaymentUseCase,
            GetManualReviewQueueUseCase getManualReviewQueueUseCase) {
        this.createPaymentUseCase = createPaymentUseCase;
        this.getPaymentUseCase = getPaymentUseCase;
        this.getManualReviewQueueUseCase = getManualReviewQueueUseCase;
    }

    public Mono<ServerResponse> createPayment(ServerRequest request) {
        return request.bodyToMono(CreatePaymentRequest.class)
                .flatMap(createPaymentUseCase::execute)
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }

    public Mono<ServerResponse> getPayment(ServerRequest request) {
        String paymentId = request.pathVariable("id");

        return getPaymentUseCase.execute(paymentId)
                .flatMap(response -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }

    public Mono<ServerResponse> getManualReviewQueue(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(getManualReviewQueueUseCase.execute(), ManualReviewQueueItemResponse.class);
    }
}
