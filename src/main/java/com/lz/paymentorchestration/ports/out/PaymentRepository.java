package com.lz.paymentorchestration.ports.out;

import com.lz.paymentorchestration.domain.payment.Payment;
import com.lz.paymentorchestration.domain.payment.enums.PaymentStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PaymentRepository {
    Mono<Payment> save(Payment payment);

    Mono<Payment> findById(String paymentId);

    Mono<Payment> findByProviderPaymentId(String providerPaymentId);

    Mono<Payment> findByOrderIdAndPaymentMethod(String orderId, String paymentMethod);

    Flux<Payment> findByStatus(PaymentStatus status);
}