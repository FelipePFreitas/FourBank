# FourBank API

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Compose-blue.svg)](https://www.docker.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)

API REST para onboarding bancario (PF/PJ), autenticacao JWT, contas e transacoes bancarias.

## Funcionalidades implementadas

- Cadastro de cliente PF: `POST /clientes/pf`
- Cadastro de cliente PJ: `POST /clientes/pj`
- Login com emissao de JWT: `POST /auth/login`
- Cadastro de chave Pix da conta autenticada: `POST /contas/pix`
- Consulta de dados da conta autenticada: `GET /contas`
- Transferencia Pix: `POST /transacoes/pix/{chavePix}/{valor}`
- Transferencia bancaria imediata ou agendada: `POST /transacoes/transferencias`
- Tratamento centralizado de erros com `ProblemDetail`
- Persistencia com Spring Data JPA (PostgreSQL)

### Transferencia bancaria

O endpoint `POST /transacoes/transferencias` exige JWT e recebe os dados do favorecido:

```json
{
  "nome": "Maria da Silva",
  "documento": "52998224725",
  "banco": "FOURBANK",
  "agencia": "0001",
  "conta": "1234567",
  "tipoConta": "CC",
  "valor": 100.00,
  "agendadaPara": null
}
```

`tipoConta` aceita `CC` ou `CP`. O campo `agendadaPara` e opcional; quando informado,
a data deve ser futura e cair em dia util. Transferencias imediatas sao processadas
em dias uteis, das 08:00 as 17:00, no horario de Brasilia.

As regras de saldo, limite, taxas e transferencias gratuitas sao aplicadas no service.
Agendamentos sem saldo no momento da efetivacao sao cancelados. Cada transacao
mantem a referencia das contas de origem e destino para auditoria, e os eventos
principais sao registrados nos logs da aplicacao.

## Arquitetura

```text
com.felipefreitas.FourBank
├── controller/   # Endpoints REST
├── service/      # Regras de negocio
├── repository/   # Spring Data JPA
├── entity/       # Entidades persistidas
├── dto/          # Contratos de entrada e saida
├── security/     # JWT, filtros e UserDetailsService
├── config/       # Security e OpenAPI
├── exceptions/   # Excecoes e handler global
├── enums/        # Tipos e codigos de erro
└── utils/        # Utilitarios (CPF/CNPJ/CEP)
```

## Stack tecnica

- Java 21
- Spring Boot (Web, Data JPA, Security, Validation, Actuator)
- JWT (Auth0 `java-jwt`)
- OpenAPI/Swagger (`springdoc-openapi`)
- PostgreSQL, Redis e RabbitMQ
- Docker Compose e Testcontainers

## Redis e RabbitMQ

O Redis e utilizado para:

- Cache das consultas de conta com TTL de 5 minutos.
- Invalidacao do cache apos alteracoes de saldo.
- Idempotencia dos eventos recebidos do RabbitMQ por 24 horas.

O RabbitMQ e utilizado para publicar eventos de transferencias concluidas
apos o commit da transacao no PostgreSQL. A aplicacao consome esses eventos
para auditoria e evita o processamento duplicado usando o Redis.

- Exchange: `fourbank.transferencias`
- Fila: `fourbank.transferencias.concluidas`
- Routing key: `transferencia.concluida`

## Infra local (compose.yaml)

- PostgreSQL (`localhost:5432`)
- RabbitMQ (`localhost:5672` e UI em `localhost:15672`)
- Redis (`localhost:6379`)

## Como executar

Pre-requisitos: Java 21, Docker e Docker Compose.

1. Subir infraestrutura:
   ```bash
   docker compose up -d
   ```
2. Executar API:
   ```bash
   .\mvnw spring-boot:run
   ```

## Documentacao e autenticacao

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Health check: `http://localhost:8080/actuator/health`

Endpoints publicos:

- `POST /auth/login`
- `POST /clientes/pf`
- `POST /clientes/pj`
- Rotas de documentacao (`/swagger-ui/**`, `/v3/api-docs/**`)

Demais rotas exigem JWT no header `Authorization` no formato Bearer token.

## Testes

```bash
.\mvnw test
```

Para executar somente os testes unitarios dos services:

```bash
.\mvnw "-Dtest=TransacaoServiceTest,ContaServiceTest,ClientePFServiceTest,ClientePJServiceTest,AuthServiceTest" test
```

## Configuracao das transferencias

Os valores abaixo podem ser sobrescritos por variaveis de ambiente:

| Propriedade | Padrao | Descricao |
| --- | --- | --- |
| `TRANSFERENCIA_LIMITE` | `5000.00` | Limite por transferencia |
| `TRANSFERENCIA_TAXA` | `2.00` | Taxa apos o limite de gratuidades |
| `TRANSFERENCIA_GRATUITAS` | `3` | Quantidade diaria de transferencias sem taxa |
| `TRANSFERENCIA_HORARIO_INICIAL` | `08:00` | Inicio do horario bancario |
| `TRANSFERENCIA_HORARIO_FINAL` | `17:00` | Fim do horario bancario |
| `TRANSFERENCIA_AGENDAMENTO_INTERVALO_MS` | `5000` | Intervalo do processador de agendamentos |

## Frontend Angular

O frontend está em `frontend/` e usa Angular standalone, Reactive Forms, rotas protegidas por JWT e chamadas tipadas para a API.

```bash
cd frontend
npm install
npm start
```

Acesse `http://localhost:4200`. O proxy de desenvolvimento encaminha `/api` para `http://localhost:8080`, evitando CORS local.

Telas disponíveis:

- Login e logout com token Bearer.
- Dashboard com saldo, agência e conta.
- Depósito, saque, Pix e transferência imediata/agendada.
- Histórico local das operações retornadas pela API durante a sessão.

O backend atual não possui um endpoint GET de histórico; por isso, o frontend não inventa dados e mantém somente as transações efetivamente retornadas pelas operações realizadas.
