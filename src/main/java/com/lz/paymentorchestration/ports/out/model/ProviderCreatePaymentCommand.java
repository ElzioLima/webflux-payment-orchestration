package com.lz.paymentorchestration.ports.out.model;

import com.lz.paymentorchestration.domain.payment.enums.PaymentMethod;
import com.lz.paymentorchestration.domain.payment.vo.Money;

public record ProviderCreatePaymentCommand(
        String externalReference,
        Money amount,
        String description,
        PaymentMethod paymentMethod) {
}