# FourBank API

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Compose-blue.svg)](https://www.docker.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)

API REST para onboarding de clientes bancarios (PF/PJ) com autenticacao JWT, desenvolvida com Java e Spring Boot.

## Estado atual do projeto

Funcionalidades implementadas no codigo:

- Cadastro de cliente PF (`POST /clientes/pf`) com validacao de CPF e criacao de usuario.
- Cadastro de cliente PJ (`POST /clientes/pj`) com validacao de CNPJ e criacao de usuario.
- Login (`POST /auth/login`) com emissao de token Bearer JWT.
- Tratamento centralizado de erros de regra de negocio com `ProblemDetail`.
- Persistencia com Spring Data JPA (PostgreSQL).

Ja existem entidades e repositorios para `Conta` e `Transacao`, mas os endpoints desses dominios ainda nao estao expostos.

## Arquitetura real (atual)

O projeto esta organizado em arquitetura em camadas:

```text
com.felipefreitas.FourBank
├── controller/   # Endpoints REST (AuthController, ClienteController)
├── service/      # Regras de negocio e orquestracao de cadastro/login
├── repository/   # Interfaces Spring Data JPA
├── entity/       # Modelos persistidos no PostgreSQL
├── dto/          # Contratos de entrada e saida da API
├── security/     # JWT, filtro de autenticacao e UserDetailsService
├── config/       # Configuracoes de Security e OpenAPI
├── exceptions/   # Excecoes de dominio e handler global
├── enums/        # Tipos e codigos de erro
└── utils/        # Validadores utilitarios (CPF/CNPJ/CEP)
```

## Stack tecnica

- Java 21
- Spring Boot (Web, Data JPA, Security, Validation, Actuator)
- JWT (Auth0 `java-jwt`)
- OpenAPI/Swagger (`springdoc-openapi`)
- PostgreSQL, Redis e RabbitMQ
- Docker Compose e Testcontainers

## Infraestrutura local (compose.yaml)

Servicos definidos para desenvolvimento:

- PostgreSQL (`localhost:5432`)
- RabbitMQ (`localhost:5672` e painel em `localhost:15672`)
- Redis (`localhost:6379`)

## Como executar

Pre-requisitos: Java 21, Docker e Docker Compose.

1. Suba a infraestrutura:
   ```bash
   docker compose up -d
   ```
2. Execute a aplicacao:
   ```bash
   .\mvnw spring-boot:run
   ```

## Documentacao e endpoints

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- Health check: `http://localhost:8080/actuator/health`

Endpoints publicos:

- `POST /auth/login`
- `POST /clientes/pf`
- `POST /clientes/pj`

Demais rotas exigem autenticacao com token JWT no header `Authorization: Bearer <token>`.

## Testes

```bash
.\mvnw test
```
