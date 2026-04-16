package com.lz.paymentorchestration.ports.out;

import com.lz.paymentorchestration.application.service.model.ManualReviewCustomerData;

public interface CustomerReviewDataGateway {
    ManualReviewCustomerData findByOrderId(String orderId);
}