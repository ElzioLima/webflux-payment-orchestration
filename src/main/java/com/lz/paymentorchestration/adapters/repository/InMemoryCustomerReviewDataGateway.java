package com.lz.paymentorchestration.adapters.repository;

import com.lz.paymentorchestration.application.service.model.CustomerSegment;
import com.lz.paymentorchestration.application.service.model.ManualReviewCustomerData;
import com.lz.paymentorchestration.ports.out.CustomerReviewDataGateway;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryCustomerReviewDataGateway implements CustomerReviewDataGateway {

    private final Map<String, ManualReviewCustomerData> storage = new ConcurrentHashMap<>();

    public InMemoryCustomerReviewDataGateway() {
        storage.put("ord-100", new ManualReviewCustomerData(CustomerSegment.STANDARD, false, 0));
        storage.put("ord-200", new ManualReviewCustomerData(CustomerSegment.HIGH_RISK, true, 1));
        storage.put("ord-400", new ManualReviewCustomerData(CustomerSegment.HIGH_RISK, false, 2));
        storage.put("ord-500", new ManualReviewCustomerData(CustomerSegment.VIP, false, 0));
    }

    @Override
    public ManualReviewCustomerData findByOrderId(String orderId) {
        simulateBlockingLatency();
        return storage.getOrDefault(orderId, ManualReviewCustomerData.unknown());
    }

    private void simulateBlockingLatency() {
        try {
            Thread.sleep(120);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Blocking enrichment interrupted", ex);
        }
    }
}