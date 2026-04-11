package com.lz.paymentorchestration.adapters.repository;

import com.lz.paymentorchestration.domain.payment.Order;
import com.lz.paymentorchestration.domain.payment.enums.OrderStatus;
import com.lz.paymentorchestration.domain.payment.vo.Money;
import com.lz.paymentorchestration.ports.out.OrderRepository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryOrderRepository implements OrderRepository {

        private final Map<String, Order> storage = new ConcurrentHashMap<>();

        public InMemoryOrderRepository() {
                storage.put(
                                "ord-100",
                                new Order(
                                                "ord-100",
                                                "cust-1",
                                                Money.of(new BigDecimal("149.90"), "BRL"),
                                                OrderStatus.PENDING_PAYMENT,
                                                false));

                storage.put(
                                "ord-200",
                                new Order(
                                                "ord-200",
                                                "cust-2",
                                                Money.of(new BigDecimal("399.90"), "BRL"),
                                                OrderStatus.PENDING_PAYMENT,
                                                true));

                storage.put(
                                "ord-300",
                                new Order(
                                                "ord-300",
                                                "cust-3",
                                                Money.of(new BigDecimal("89.90"), "BRL"),
                                                OrderStatus.CANCELLED,
                                                false));

                storage.put(
                                "ord-400",
                                new Order(
                                                "ord-400",
                                                "cust-4",
                                                Money.of(new BigDecimal("1500.00"), "BRL"),
                                                OrderStatus.PENDING_PAYMENT,
                                                false));

                storage.put(
                                "ord-500",
                                new Order(
                                                "ord-500",
                                                "cust-5",
                                                Money.of(new BigDecimal("6000.00"), "BRL"),
                                                OrderStatus.PENDING_PAYMENT,
                                                false));
        }

        @Override
        public Mono<Order> findById(String orderId) {
                Order order = storage.get(orderId);
                return order != null ? Mono.just(order) : Mono.empty();
        }
}