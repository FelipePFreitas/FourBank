# 🏦 FourBank — Ecossistema Bancário Digital completo

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Supported-blue.svg)](https://www.docker.com/)
[![Architecture](https://img.shields.io/badge/Architecture-Hexagonal-purple.svg)]()
[![License](https://img.shields.io/badge/License-MIT-green.svg)]()

> API RESTful bancária de alta complexidade desenvolvida em **Java 21** e **Spring Boot 3**, baseada estritamente nos princípios da **Arquitetura Hexagonal (Ports & Adapters)**, **Domain-Driven Design (DDD)** e resiliência financeira. O sistema gerencia o ciclo completo de operações de uma instituição financeira moderna: contas, cartões, empréstimos, pagamentos instantâneos (Pix) e auditoria.

---

## 🚀 Módulos do Sistema

O **FourBank** não se limita à gestão de contas. A plataforma integra múltiplos módulos e domínios bancários de forma desacoplada:

* 👤 **Clientes & Onboarding (PF/PJ):** Cadastro e validação de Pessoas Físicas (CPF) e Jurídicas (CNPJ), análise de perfil de risco e limites operacionais.
* 🏦 **Contas & Saldos:** Gestão de Contas Correntes e Poupança, controle de saldo em tempo real, extratos detalhados e bloqueios judiciais/operacionais.
* 💳 **Gestão de Cartões:** Emissão e gerenciamento de cartões virtuais e físicos (débito/crédito), faturas, alteração de limites e bloqueio temporário por suspeita de fraude.
* 💸 **Pix & Transações Financeiras:** Processamento atômico (ACID) de Pix, TED, DOC, saques e depósitos com garantia de idempotência e taxa de processamento otimizada.
* 📑 **Empréstimos & Crédito:** Simulação, contratação, parcelamento com cálculo de juros compostos e liquidação adiantada de contratos de crédito.
* 🔐 **Segurança & Identidade:** Autenticação via OAuth2 / JWT, controle de acesso granular baseado em perfis (RBAC) e proteção contra ataques (*rate-limiting* via Redis).
* 🔔 **Mensageria & Notificações:** Emissão assíncrona de comprovantes, envio de push/e-mail transacional e trilha de auditoria via RabbitMQ.

---

## 📐 Arquitetura Hexagonal & Estrutura do Projeto

O core de negócios é mantido em **Java puro** (zero dependências de frameworks no pacote `domain`), garantindo independência total de bancos de dados, frameworks HTTP ou Message Brokers.

```text
com.felipefreitas.FourBank
├── domain/                      # 🧠 NÚCLEO DE NEGÓCIOS (Java Puro)
│   ├── model/                   # Entidades e Agregados (Cliente, Conta, Cartao, Emprestimo, Pix)
│   ├── exception/               # Exceções de Domínio (ex: SaldoInsuficienteException)
│   └── service/                 # Regras de Negócio e Validadores de Domínio
│
├── ports/                       # 🔌 CONTRATOS E INTERFACES
│   ├── in/                      # Driving Ports (Casos de uso chamados pela API REST/Listeners)
│   │   ├── cliente/             # CadastrarClientePFUseCase, CadastrarClientePJUseCase
│   │   ├── conta/               # CriarContaUseCase, ConsultarExtratoUseCase
│   │   ├── cartao/              # SolicitarCartaoUseCase, BloquearCartaoUseCase
│   │   ├── transacao/           # ProcessarPixUseCase, RealizarTedUseCase
│   │   └── emprestimo/          # SimularEmprestimoUseCase, ContratarCreditoUseCase
│   └── out/                     # Driven Ports (Interfaces de Saída do Domínio)
│       ├── persistence/         # SalvarClientePort, BuscarContaPort, SalvarTransacaoPort
│       ├── messaging/           # PublicarComprovantePort, NotificarFraudePort
│       └── cache/               # CacheSaldoPort, ControlarRateLimitPort
│
└── adapters/                    # 🛠️ INFRAESTRUTURA & FRAMEWORKS (Spring Boot)
    ├── in/                      # Driving Adapters (Entradas da aplicação)
    │   ├── web/                 # Controllers REST e DTOs de Entrada/Saída
    │   └── messaging/           # RabbitMQ Listeners (Consumo de filas)
    └── out/                     # Driven Adapters (Saídas da aplicação)
        ├── persistence/         # Mapeadores, JPA Entities, Repositories e Adapters do Postgres
        ├── messaging/           # Producers do RabbitMQ
        ├── cache/               # Implementação do Redis Template
        └── security/            # Spring Security, OAuth2 Resource Server e JWT Token Provider
