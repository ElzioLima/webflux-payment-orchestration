# Payment Orchestration API

API reativa em Java 17 com Spring WebFlux para orquestração de pagamentos com:

- precheck interno
- idempotência simples
- integração desacoplada com provider
- webhook assíncrono
- persistência utilitária em memória
- arquitetura orientada a domínio

## Stack

- Java 17
- Spring Boot
- Spring WebFlux
- Maven

## Rotas

### Criar pagamento
`POST /payments`

### Consultar pagamento
`GET /payments/{id}`

### Processar webhook do provider
`POST /payments/webhooks/provider`

## Fluxo principal

1. recebe pedido de criação
2. valida regras internas
3. verifica idempotência por `orderId + paymentMethod`
4. cria pagamento interno
5. envia ao provider por adapter
6. persiste `providerPaymentId`
7. recebe atualização assíncrona via webhook
8. atualiza status interno

## Regras internas

- valor deve ser maior que zero
- valor deve bater com o total do pedido
- cliente não pode estar bloqueado
- pedido deve estar pendente de pagamento
- valor acima do limite máximo é rejeitado
- valor acima do threshold entra em revisão manual

## Status internos

- `CREATED`
- `PENDING_PROVIDER`
- `AUTHORIZED`
- `PAID`
- `FAILED`
- `CANCELLED`
- `MANUAL_REVIEW`

## Como rodar

```bash
./mvnw spring-boot:run