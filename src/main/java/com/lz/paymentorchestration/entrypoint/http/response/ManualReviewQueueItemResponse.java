package com.lz.paymentorchestration.entrypoint.http.response;

import com.lz.paymentorchestration.application.service.model.CustomerSegment;
import com.lz.paymentorchestration.application.service.model.ManualReviewQueueCategory;
import com.lz.paymentorchestration.domain.payment.enums.PaymentMethod;
import com.lz.paymentorchestration.domain.payment.enums.PaymentProvider;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ManualReviewQueueItemResponse(
        String paymentId,
        String orderId,
        PaymentMethod paymentMethod,
        PaymentProvider provider,
        BigDecimal amount,
        String currency,
        ManualReviewQueueCategory queueCategory,
        boolean actionable,
        CustomerSegment customerSegment,
        boolean customerBlocked,
        int previousManualReviewCount,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}