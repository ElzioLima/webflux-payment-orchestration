package com.lz.paymentorchestration.entrypoint.http.response;

import java.time.OffsetDateTime;

public record ErrorResponse(
                String code,
                String message,
                int status,
                String path,
                OffsetDateTime timestamp) {
}