# Payment Orchestration API

API reativa em Java 17 com Spring WebFlux para orquestração de pagamentos.

## Funcionalidades

- precheck interno
- idempotência (`orderId + paymentMethod`)
- integração desacoplada com provider
- webhook assíncrono
- persistência em memória
- fila operacional de revisão manual (V9)

## Stack

- Java 17
- Spring Boot
- Spring WebFlux
- Maven

## Rotas

- POST /payments
- GET /payments/{id}
- GET /payments/manual-review/queue
- POST /payments/webhooks/provider

## Fluxo principal

1. recebe requisição
2. valida regras internas
3. verifica idempotência
4. cria pagamento
5. envia ao provider
6. persiste providerPaymentId
7. recebe webhook
8. atualiza status
9. pagamentos podem entrar na fila de revisão manual:
    - busca pagamentos em `MANUAL_REVIEW`
    - limita o volume processado
    - enriquece com dados externos (bloqueante)
    - classifica para análise operacional

## Regras internas

- valor > 0
- valor == pedido
- cliente não bloqueado
- pedido pendente
- valor > limite → rejeita
- valor > threshold → MANUAL_REVIEW

## Classificação da fila

- BLOCKED_CUSTOMER_ESCALATION
- HIGH_VALUE
- REANALYSIS
- STANDARD_REVIEW

## Status

- CREATED
- PENDING_PROVIDER
- AUTHORIZED
- PAID
- FAILED
- CANCELLED
- MANUAL_REVIEW

## Configuração

server.port=8080

payment.manual-review.high-value=3000
payment.manual-review.queue.batch-size=20
payment.manual-review.queue.enrichment-concurrency=4
payment.manual-review.queue.enrichment-timeout-seconds=2

## Run

./mvnw spring-boot:run
