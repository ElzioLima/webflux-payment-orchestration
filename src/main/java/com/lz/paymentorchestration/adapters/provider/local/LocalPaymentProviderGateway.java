package com.lz.paymentorchestration.adapters.provider.local;

import com.lz.paymentorchestration.ports.out.PaymentProviderGateway;
import com.lz.paymentorchestration.ports.out.model.ProviderCreatePaymentCommand;
import com.lz.paymentorchestration.ports.out.model.ProviderPaymentResult;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class LocalPaymentProviderGateway implements PaymentProviderGateway {

    @Override
    public Mono<ProviderPaymentResult> createPayment(ProviderCreatePaymentCommand command) {
        String providerPaymentId = "prov-" + UUID.randomUUID();

        return Mono.just(new ProviderPaymentResult(
                providerPaymentId,
                "pending"));
    }

    @Override
    public Mono<ProviderPaymentResult> getPaymentStatus(String providerPaymentId) {
        return Mono.just(new ProviderPaymentResult(
                providerPaymentId,
                "pending"));
    }
}