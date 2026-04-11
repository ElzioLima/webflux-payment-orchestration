package com.lz.paymentorchestration.ports.out;

import reactor.core.publisher.Mono;

public interface AuditRepository {
    Mono<Void> register(String eventType, String payload);
}