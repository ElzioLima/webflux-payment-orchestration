package com.lz.paymentorchestration.adapters.repository;

import com.lz.paymentorchestration.ports.out.AuditRepository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InMemoryAuditRepository implements AuditRepository {

    private final List<String> events = new CopyOnWriteArrayList<>();

    @Override
    public Mono<Void> register(String eventType, String payload) {
        events.add(eventType + " :: " + payload);
        return Mono.empty();
    }

    public List<String> getEvents() {
        return List.copyOf(events);
    }
}