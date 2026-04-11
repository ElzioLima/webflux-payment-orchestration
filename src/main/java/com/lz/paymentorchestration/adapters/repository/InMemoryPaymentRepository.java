package com.lz.paymentorchestration.adapters.repository;

import com.lz.paymentorchestration.domain.payment.Payment;
import com.lz.paymentorchestration.ports.out.PaymentRepository;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryPaymentRepository implements PaymentRepository {

    private final Map<String, Payment> storage = new ConcurrentHashMap<>();

    @Override
    public Mono<Payment> save(Payment payment) {
        storage.put(payment.getId().value(), payment);
        return Mono.just(payment);
    }

    @Override
    public Mono<Payment> findById(String paymentId) {
        Payment payment = storage.get(paymentId);
        return payment != null ? Mono.just(payment) : Mono.empty();
    }

    @Override
    public Mono<Payment> findByProviderPaymentId(String providerPaymentId) {
        return Mono.justOrEmpty(
                storage.values()
                        .stream()
                        .filter(payment -> payment.getProviderPaymentId() != null &&
                                payment.getProviderPaymentId().value().equals(providerPaymentId))
                        .findFirst());
    }

    @Override
    public Mono<Payment> findByOrderIdAndPaymentMethod(String orderId, String paymentMethod) {
        return Mono.justOrEmpty(
                storage.values()
                        .stream()
                        .filter(payment -> payment.getOrderId().equals(orderId) &&
                                payment.getPaymentMethod().name().equals(paymentMethod))
                        .filter(payment -> !payment.getStatus().isFinalStatus())
                        .findFirst());
    }
}