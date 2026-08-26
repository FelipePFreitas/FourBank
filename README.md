# FourBank API

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Compose-blue.svg)](https://www.docker.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)

API REST para onboarding bancario (PF/PJ), autenticacao JWT, conta e transacoes Pix.

## Funcionalidades implementadas

- Cadastro de cliente PF: `POST /clientes/pf`
- Cadastro de cliente PJ: `POST /clientes/pj`
- Login com emissao de JWT: `POST /auth/login`
- Cadastro de chave Pix da conta autenticada: `POST /contas/pix`
- Consulta de dados da conta autenticada: `GET /contas`
- Transferencia Pix: `POST /transacoes/pix/{chavePix}/{valor}`
- Tratamento centralizado de erros com `ProblemDetail`
- Persistencia com Spring Data JPA (PostgreSQL)

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
