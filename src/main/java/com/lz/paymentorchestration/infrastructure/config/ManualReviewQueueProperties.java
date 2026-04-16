package com.lz.paymentorchestration.infrastructure.config;

import java.math.BigDecimal;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.manual-review.queue")
public class ManualReviewQueueProperties {

    private int batchSize;
    private int enrichmentConcurrency;
    private long enrichmentTimeoutSeconds;
    private BigDecimal highValue;

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getEnrichmentConcurrency() {
        return enrichmentConcurrency;
    }

    public void setEnrichmentConcurrency(int enrichmentConcurrency) {
        this.enrichmentConcurrency = enrichmentConcurrency;
    }

    public long getEnrichmentTimeoutSeconds() {
        return enrichmentTimeoutSeconds;
    }

    public void setEnrichmentTimeoutSeconds(long enrichmentTimeoutSeconds) {
        this.enrichmentTimeoutSeconds = enrichmentTimeoutSeconds;
    }

    public BigDecimal getHighValue() {
        return highValue;
    }

    public void setHighValue(BigDecimal highValue) {
        this.highValue = highValue;
    }
}