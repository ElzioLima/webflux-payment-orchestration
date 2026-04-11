package com.lz.paymentorchestration.entrypoint.http.router;

import com.lz.paymentorchestration.entrypoint.http.handler.PaymentHandler;
import com.lz.paymentorchestration.entrypoint.http.handler.PaymentWebhookHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;

@Configuration
public class PaymentRouter {

    @Bean
    public RouterFunction<ServerResponse> routes(
            PaymentHandler paymentHandler,
            PaymentWebhookHandler paymentWebhookHandler) {
        return route(POST("/payments"), paymentHandler::createPayment)
                .andRoute(GET("/payments/{id}"), paymentHandler::getPayment)
                .andRoute(POST("/payments/webhooks/provider"), paymentWebhookHandler::handleProviderWebhook);
    }
}