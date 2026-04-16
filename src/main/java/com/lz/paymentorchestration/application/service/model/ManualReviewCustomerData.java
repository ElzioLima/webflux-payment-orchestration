package com.lz.paymentorchestration.application.service.model;

public record ManualReviewCustomerData(
                CustomerSegment customerSegment,
                boolean customerBlocked,
                int previousManualReviewCount) {
        public static ManualReviewCustomerData unknown() {
                return new ManualReviewCustomerData(CustomerSegment.UNKNOWN, false, 0);
        }
}