package com.lz.paymentorchestration.application.service;

import com.lz.paymentorchestration.application.service.model.PaymentPrecheckResult;
import com.lz.paymentorchestration.domain.payment.Order;
import com.lz.paymentorchestration.domain.payment.vo.Money;

public class PaymentPrecheckService {

    private final Money maxAllowedAmount;
    private final Money manualReviewThreshold;

    public PaymentPrecheckService(Money maxAllowedAmount, Money manualReviewThreshold) {
        this.maxAllowedAmount = maxAllowedAmount;
        this.manualReviewThreshold = manualReviewThreshold;
    }

    public PaymentPrecheckResult evaluate(Order order, Money requestedAmount) {
        if (!requestedAmount.isPositive()) {
            return PaymentPrecheckResult.rejected("Payment amount must be positive");
        }

        if (!order.isPendingPayment()) {
            return PaymentPrecheckResult.rejected("Order is not pending payment");
        }

        if (order.isCustomerBlocked()) {
            return PaymentPrecheckResult.rejected("Customer is blocked");
        }

        if (!order.amountMatches(requestedAmount)) {
            return PaymentPrecheckResult.rejected("Payment amount does not match order total");
        }

        if (requestedAmount.isGreaterThan(maxAllowedAmount)) {
            return PaymentPrecheckResult.rejected("Payment amount exceeds maximum allowed limit");
        }

        if (requestedAmount.isGreaterThan(manualReviewThreshold)) {
            return PaymentPrecheckResult.manualReview("Payment amount requires manual review");
        }

        return PaymentPrecheckResult.approved();
    }
}