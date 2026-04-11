package com.lz.paymentorchestration.domain.payment;

import com.lz.paymentorchestration.domain.payment.enums.OrderStatus;
import com.lz.paymentorchestration.domain.payment.vo.Money;

import java.util.Objects;

public class Order {

    private final String id;
    private final String customerId;
    private final Money total;
    private final OrderStatus status;
    private final boolean customerBlocked;

    public Order(
            String id,
            String customerId,
            Money total,
            OrderStatus status,
            boolean customerBlocked) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.customerId = Objects.requireNonNull(customerId, "customerId must not be null");
        this.total = Objects.requireNonNull(total, "total must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.customerBlocked = customerBlocked;
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public Money getTotal() {
        return total;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public boolean isCustomerBlocked() {
        return customerBlocked;
    }

    public boolean isPendingPayment() {
        return status == OrderStatus.PENDING_PAYMENT;
    }

    public boolean canAcceptPayment() {
        return isPendingPayment() && !customerBlocked;
    }

    public boolean amountMatches(Money paymentAmount) {
        return total.isEqualTo(paymentAmount);
    }
}