package com.lz.paymentorchestration.infrastructure.config;

import com.lz.paymentorchestration.adapters.repository.InMemoryAuditRepository;
import com.lz.paymentorchestration.adapters.repository.InMemoryCustomerReviewDataGateway;
import com.lz.paymentorchestration.adapters.repository.InMemoryOrderRepository;
import com.lz.paymentorchestration.adapters.repository.InMemoryPaymentRepository;
import com.lz.paymentorchestration.application.service.PaymentPrecheckService;
import com.lz.paymentorchestration.application.service.ProviderStatusMapper;
import com.lz.paymentorchestration.application.usecase.CreatePaymentUseCase;
import com.lz.paymentorchestration.application.usecase.GetManualReviewQueueUseCase;
import com.lz.paymentorchestration.application.usecase.GetPaymentUseCase;
import com.lz.paymentorchestration.application.usecase.HandlePaymentWebhookUseCase;
import com.lz.paymentorchestration.application.usecase.impl.CreatePaymentUseCaseImpl;
import com.lz.paymentorchestration.application.usecase.impl.GetManualReviewQueueUseCaseImpl;
import com.lz.paymentorchestration.application.usecase.impl.GetPaymentUseCaseImpl;
import com.lz.paymentorchestration.application.usecase.impl.HandlePaymentWebhookUseCaseImpl;
import com.lz.paymentorchestration.domain.payment.policy.ManualReviewQueuePolicy;
import com.lz.paymentorchestration.domain.payment.vo.Money;
import com.lz.paymentorchestration.entrypoint.http.handler.ApiErrorHandler;
import com.lz.paymentorchestration.entrypoint.http.handler.PaymentHandler;
import com.lz.paymentorchestration.entrypoint.http.handler.PaymentWebhookHandler;
import com.lz.paymentorchestration.ports.out.AuditRepository;
import com.lz.paymentorchestration.ports.out.CustomerReviewDataGateway;
import com.lz.paymentorchestration.ports.out.OrderRepository;
import com.lz.paymentorchestration.ports.out.PaymentProviderGateway;
import com.lz.paymentorchestration.ports.out.PaymentRepository;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@EnableConfigurationProperties({
        ProviderProperties.class,
        ManualReviewQueueProperties.class
})

@Configuration
public class ApplicationConfig {

    @Bean
    public PaymentRepository paymentRepository() {
        return new InMemoryPaymentRepository();
    }

    @Bean
    public OrderRepository orderRepository() {
        return new InMemoryOrderRepository();
    }

    @Bean
    public AuditRepository auditRepository() {
        return new InMemoryAuditRepository();
    }

    @Bean
    public ProviderStatusMapper providerStatusMapper() {
        return new ProviderStatusMapper();
    }

    @Bean
    public PaymentPrecheckService paymentPrecheckService() {
        return new PaymentPrecheckService(
                Money.of(new BigDecimal("5000.00"), "BRL"),
                Money.of(new BigDecimal("1000.00"), "BRL"));
    }

    @Bean
    public CreatePaymentUseCase createPaymentUseCase(
            OrderRepository orderRepository,
            PaymentRepository paymentRepository,
            PaymentProviderGateway paymentProviderGateway,
            ProviderStatusMapper providerStatusMapper,
            PaymentPrecheckService paymentPrecheckService) {
        return new CreatePaymentUseCaseImpl(
                orderRepository,
                paymentRepository,
                paymentProviderGateway,
                providerStatusMapper,
                paymentPrecheckService);
    }

    @Bean
    public GetPaymentUseCase getPaymentUseCase(PaymentRepository paymentRepository) {
        return new GetPaymentUseCaseImpl(paymentRepository);
    }

    @Bean
    public CustomerReviewDataGateway customerReviewDataGateway() {
        return new InMemoryCustomerReviewDataGateway();
    }

    @Bean
    public ManualReviewQueuePolicy manualReviewQueuePolicy(ManualReviewQueueProperties manualReviewQueueProperties) {
        return new ManualReviewQueuePolicy(manualReviewQueueProperties);
    }

    @Bean
    public GetManualReviewQueueUseCase getManualReviewQueueUseCase(
            PaymentRepository paymentRepository,
            CustomerReviewDataGateway customerReviewDataGateway,
            ManualReviewQueuePolicy manualReviewQueuePolicy,
            ManualReviewQueueProperties manualReviewQueueProperties) {
        return new GetManualReviewQueueUseCaseImpl(
                paymentRepository,
                customerReviewDataGateway,
                manualReviewQueuePolicy,
                manualReviewQueueProperties);
    }

    @Bean
    public HandlePaymentWebhookUseCase handlePaymentWebhookUseCase(
            PaymentRepository paymentRepository,
            AuditRepository auditRepository,
            ProviderStatusMapper providerStatusMapper) {
        return new HandlePaymentWebhookUseCaseImpl(
                paymentRepository,
                auditRepository,
                providerStatusMapper);
    }

    @Bean
    public PaymentHandler paymentHandler(
            CreatePaymentUseCase createPaymentUseCase,
            GetPaymentUseCase getPaymentUseCase,
            GetManualReviewQueueUseCase getManualReviewQueueUseCase) {
        return new PaymentHandler(
                createPaymentUseCase,
                getPaymentUseCase,
                getManualReviewQueueUseCase);
    }

    @Bean
    public PaymentWebhookHandler paymentWebhookHandler(
            HandlePaymentWebhookUseCase handlePaymentWebhookUseCase) {
        return new PaymentWebhookHandler(handlePaymentWebhookUseCase);
    }

    @Bean
    public ApiErrorHandler apiErrorHandler() {
        return new ApiErrorHandler();
    }
}
