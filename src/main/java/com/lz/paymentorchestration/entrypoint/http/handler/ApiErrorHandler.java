package com.lz.paymentorchestration.entrypoint.http.handler;

import com.lz.paymentorchestration.entrypoint.http.error.ApiBusinessException;
import com.lz.paymentorchestration.entrypoint.http.error.ApiErrorCode;
import com.lz.paymentorchestration.entrypoint.http.response.ErrorResponse;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;

@Order(-2)
public class ApiErrorHandler implements WebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ErrorDescriptor descriptor = mapError(ex, exchange);

        ErrorResponse error = new ErrorResponse(
                descriptor.code.name(),
                descriptor.message,
                descriptor.status.value(),
                exchange.getRequest().getPath().value(),
                OffsetDateTime.now());

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(descriptor.status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = """
                {"code":"%s","message":"%s","status":%d,"path":"%s","timestamp":"%s"}
                """.formatted(
                error.code(),
                escape(error.message()),
                error.status(),
                escape(error.path()),
                error.timestamp());

        DataBufferFactory bufferFactory = response.bufferFactory();
        return response.writeWith(
                Mono.just(bufferFactory.wrap(body.getBytes(StandardCharsets.UTF_8))));
    }

    private ErrorDescriptor mapError(Throwable ex, ServerWebExchange exchange) {
        if (ex instanceof ApiBusinessException businessException) {
            return new ErrorDescriptor(
                    businessException.getCode(),
                    businessException.getMessage(),
                    businessException.getStatus());
        }

        return new ErrorDescriptor(
                ApiErrorCode.INTERNAL_ERROR,
                ex.getMessage() != null ? ex.getMessage() : "Unexpected internal error",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String escape(String value) {
        return value.replace("\"", "\\\"");
    }

    private record ErrorDescriptor(
            ApiErrorCode code,
            String message,
            HttpStatus status) {
    }
}