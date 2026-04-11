package com.lz.paymentorchestration.entrypoint.http.error;

import org.springframework.http.HttpStatus;

public class ApiBusinessException extends RuntimeException {

    private final ApiErrorCode code;
    private final HttpStatus status;

    public ApiBusinessException(ApiErrorCode code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public ApiErrorCode getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }
}