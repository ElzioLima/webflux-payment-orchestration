package com.lz.paymentorchestration.domain.payment.policy;

import com.lz.paymentorchestration.application.service.model.ManualReviewCustomerData;
import com.lz.paymentorchestration.application.service.model.ManualReviewQueueCategory;
import com.lz.paymentorchestration.domain.payment.Payment;
import com.lz.paymentorchestration.infrastructure.config.ManualReviewQueueProperties;

import java.util.Objects;

public class ManualReviewQueuePolicy {

    private final ManualReviewQueueProperties manualReviewProperties;

    public ManualReviewQueuePolicy(ManualReviewQueueProperties manualReviewProperties) {
        this.manualReviewProperties = Objects.requireNonNull(manualReviewProperties);
    }

    public boolean isActionable(Payment payment, ManualReviewCustomerData customerData) {
        return payment.getCreatedAt() != null
                && payment.getUpdatedAt() != null
                && !payment.isFinalStatus()
                && !customerData.customerBlocked();
    }

    public ManualReviewQueueCategory resolveCategory(Payment payment, ManualReviewCustomerData customerData) {
        if (customerData.customerBlocked()) {
            return ManualReviewQueueCategory.BLOCKED_CUSTOMER_ESCALATION;
        }

        if (payment.getAmount().amount().compareTo(manualReviewProperties.getHighValue()) >= 0) {
            return ManualReviewQueueCategory.HIGH_VALUE;
        }

        if (customerData.previousManualReviewCount() > 0) {
            return ManualReviewQueueCategory.REANALYSIS;
        }

        return ManualReviewQueueCategory.STANDARD_REVIEW;
    }
}