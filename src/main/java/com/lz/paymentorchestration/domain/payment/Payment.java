package com.lz.paymentorchestration.domain.payment;

import com.lz.paymentorchestration.domain.payment.enums.PaymentMethod;
import com.lz.paymentorchestration.domain.payment.enums.PaymentProvider;
import com.lz.paymentorchestration.domain.payment.enums.PaymentStatus;
import com.lz.paymentorchestration.domain.payment.vo.Money;
import com.lz.paymentorchestration.domain.payment.vo.PaymentId;
import com.lz.paymentorchestration.domain.payment.vo.ProviderPaymentId;

import java.time.OffsetDateTime;
import java.util.Objects;

public class Payment {

    private final PaymentId id;
    private final String orderId;
    private final PaymentMethod paymentMethod;
    private final PaymentProvider provider;
    private final Money amount;
    private final OffsetDateTime createdAt;

    private ProviderPaymentId providerPaymentId;
    private PaymentStatus status;
    private OffsetDateTime updatedAt;

    public Payment(
            PaymentId id,
            String orderId,
            PaymentMethod paymentMethod,
            PaymentProvider provider,
            ProviderPaymentId providerPaymentId,
            Money amount,
            PaymentStatus status,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.orderId = Objects.requireNonNull(orderId, "orderId must not be null");
        this.paymentMethod = Objects.requireNonNull(paymentMethod, "paymentMethod must not be null");
        this.provider = Objects.requireNonNull(provider, "provider must not be null");
        this.amount = Objects.requireNonNull(amount, "amount must not be null");
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
        this.providerPaymentId = providerPaymentId;
    }

    public static Payment newPayment(
            String orderId,
            PaymentMethod paymentMethod,
            PaymentProvider provider,
            Money amount,
            OffsetDateTime now) {
        return new Payment(
                PaymentId.newId(),
                orderId,
                paymentMethod,
                provider,
                null,
                amount,
                PaymentStatus.CREATED,
                now,
                now);
    }

    public PaymentId getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public PaymentProvider getProvider() {
        return provider;
    }

    public ProviderPaymentId getProviderPaymentId() {
        return providerPaymentId;
    }

    public Money getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isFinalStatus() {
        return status.isFinalStatus();
    }

    public void attachProviderPaymentId(ProviderPaymentId providerPaymentId, OffsetDateTime now) {
        if (this.providerPaymentId != null) {
            throw new IllegalStateException("Provider payment id already defined");
        }

        this.providerPaymentId = Objects.requireNonNull(providerPaymentId, "providerPaymentId must not be null");
        touch(now);
    }

    public void confirmProviderSubmission(ProviderPaymentId providerPaymentId, PaymentStatus providerMappedStatus,
            OffsetDateTime now) {
        attachProviderPaymentId(providerPaymentId, now);
        moveTo(providerMappedStatus, now);
    }

    public void moveTo(PaymentStatus targetStatus, OffsetDateTime now) {
        Objects.requireNonNull(targetStatus, "targetStatus must not be null");
        Objects.requireNonNull(now, "now must not be null");

        if (!status.canTransitionTo(targetStatus)) {
            throw new IllegalStateException(
                    "Invalid payment status transition: " + status + " -> " + targetStatus);
        }

        this.status = targetStatus;
        this.updatedAt = now;
    }

    public void markPendingProvider(OffsetDateTime now) {
        moveTo(PaymentStatus.PENDING_PROVIDER, now);
    }

    public void markManualReview(OffsetDateTime now) {
        moveTo(PaymentStatus.MANUAL_REVIEW, now);
    }

    public void markAuthorized(OffsetDateTime now) {
        moveTo(PaymentStatus.AUTHORIZED, now);
    }

    public void markPaid(OffsetDateTime now) {
        moveTo(PaymentStatus.PAID, now);
    }

    public void markFailed(OffsetDateTime now) {
        moveTo(PaymentStatus.FAILED, now);
    }

    public void markCancelled(OffsetDateTime now) {
        moveTo(PaymentStatus.CANCELLED, now);
    }

    private void touch(OffsetDateTime now) {
        this.updatedAt = Objects.requireNonNull(now, "now must not be null");
    }
}