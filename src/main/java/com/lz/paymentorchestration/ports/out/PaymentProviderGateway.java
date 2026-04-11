package com.lz.paymentorchestration.ports.out;

import com.lz.paymentorchestration.ports.out.model.ProviderCreatePaymentCommand;
import com.lz.paymentorchestration.ports.out.model.ProviderPaymentResult;
import reactor.core.publisher.Mono;

public interface PaymentProviderGateway {
    Mono<ProviderPaymentResult> createPayment(ProviderCreatePaymentCommand command);

    Mono<ProviderPaymentResult> getPaymentStatus(String providerPaymentId);
}