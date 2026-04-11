package com.lz.paymentorchestration.ports.out;

import com.lz.paymentorchestration.domain.payment.Order;

import reactor.core.publisher.Mono;

public interface OrderRepository {
    Mono<Order> findById(String orderId);
}
