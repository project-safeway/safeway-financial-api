# 🏦 SafeWay - Microserviço Financeiro

Microserviço responsável pelo gerenciamento financeiro do sistema SafeWay, incluindo controle de mensalidades de alunos e pagamentos de funcionários.

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen.svg)](https://spring.io/projects/spring-boot)

---

## 📋 Sobre o Projeto

O **SafeWay Financeiro** é um microserviço desenvolvido com **Clean Architecture** para gerenciar operações financeiras de vans escolares, separado do sistema principal para garantir:

- ✅ **Escalabilidade independente** - Escala apenas o que precisa
- ✅ **Manutenibilidade** - Mudanças isoladas não afetam outros serviços
- ✅ **Responsabilidade única** - Foco exclusivo em operações financeiras
- ✅ **Testabilidade** - Lógica de negócio isolada e testável

### **Funcionalidades Principais**

- 📊 Controle de mensalidades de alunos
- 💰 Registro de pagamentos de funcionários
- 🔍 Busca avançada com filtros combinados (AND)
- 📄 Paginação para grandes volumes de dados
- ⏰ Jobs agendados para geração automática de mensalidades

---

## 🏗 Arquitetura

Este projeto segue os princípios da **Clean Architecture**, garantindo independência de frameworks e facilitando a manutenção.

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                       │
│  (Controllers, DTOs REST, Exception Handlers)               │
└───────────────────────┬─────────────────────────────────────┘
                        │ depende de
                        ↓
┌─────────────────────────────────────────────────────────────┐
│                   APPLICATION LAYER                         │
│  (Use Cases, DTOs internos, Output Ports)                   │
└───────────────────────┬─────────────────────────────────────┘
                        │ depende de
                        ↓
┌─────────────────────────────────────────────────────────────┐
│                     DOMAIN LAYER                            │
│  (Entities, Repository Interfaces)           │
└─────────────────────────────────────────────────────────────┘
                        ↑
                        │ implementa
                        │
┌─────────────────────────────────────────────────────────────┐
│                 INFRASTRUCTURE LAYER                        │
│  (JPA, Feign Clients, Mappers, Schedulers)                  │
└─────────────────────────────────────────────────────────────┘
```  

### **Camadas**

| Camada | Responsabilidade | Dependências |
|--------|------------------|--------------|
| **Domain** | Lógica de negócio pura | Nenhuma (Java puro) |
| **Application** | Casos de uso (orquestração) | Domain |
| **Infrastructure** | Detalhes técnicos (DB, HTTP, etc) | Domain, Application |
| **Presentation** | Interface REST | Application |

---

## 🛠️ Tecnologias Utilizadas

- **Java 21** - Linguagem principal
- **Spring Boot 3.5.5** - Framework base
- **Spring Data JPA** - Persistência de dados
- **Spring Cloud OpenFeign** - Comunicação entre microserviços
- **MySQL** - Banco de dados
- **Lombok** - Redução de boilerplate
- **Maven** - Gerenciamento de dependências
- **Hibernate** - ORM
- **JPA Specifications** - Filtros dinâmicos

---

## 📁 Estrutura de Diretórios

```
src/main/java/com/safeway/financeiro/
│
├── domain/                              # Núcleo do negócio
│   ├── entities/                        # Entidades de domínio
│   ├── enums/
│   ├── exceptions/
│   ├── repositories/                    # Interfaces (contratos)
│   └── specifications/                  # Specifications do domínio
│
├── application/                         # Casos de uso
│   ├── usecases/
│   │   ├── mensalidade/
│   │   │   ├── impl/
│   │   └── pagamento/
│   │       └── impl/
│   ├── ports/
│   │   └── output/                      # Interfaces para infra
│   └── dto/
│
├── infrastructure/                      # Implementações técnicas
│   ├── config/
│   ├── http/
│   │   ├── clients/                     # Feign Clients
│   │   └── adapters/
│   ├── messaging/
│   ├── persistence/
│   │   ├── entities/                    # Entidades JPA
│   │   ├── mappers/
│   │   ├── repositories/
│   │   │   ├── jpa/
│   │   │   └── impl/
│   │   └── specifications/
│   └── scheduler/
│
└── presentation/                        # Interface REST
    ├── controllers/
    ├── dto/
    │   ├── request/
    │   └── response/
    ├── handler/
    └── mappers/
```

---

## ⚙️ Configuração

### **application.yml**

```yaml
server:
  port: 8081

spring:
  application:
    name: safeway-financial

  datasource:
    url: jdbc:h2:mem:safeway_financial;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MySQL
    username: ${DB_USER:sa}
    password: ${DB_PASSWORD:}
    driver-class-name: org.h2.Driver

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

  h2:
    console:
      enabled: true
      path: /h2-console

safeway:
  core:
    url: ${SAFEWAY_CORE_URL:http://localhost:8080}
```

### **Variáveis de Ambiente**

| Variável | Descrição | Padrão            |
|----------|-----------|-------------------|
| `DB_USER` | Usuário do MySQL | sa                |
| `DB_PASSWORD` | Senha do MySQL |                   |
| `SAFEWAY_CORE_URL` | URL do microserviço principal | http://localhost:8080 |

---

## 🔄 Comunicação entre Microserviços

O microserviço financeiro se comunica com o **safeway-core** (microserviço principal) via:

- **HTTP/REST** usando **Spring Cloud OpenFeign**
- **Gateway Pattern** para abstrair comunicação

```
┌──────────────────────┐         HTTP GET          ┌──────────────────────┐
│  Safeway Financeiro  │  ──────────────────────>  │    Safeway Core      │
│     (port 8081)      │  /api/alunos/{id}         │    (port 8080)       │
│                      │  <──────────────────────  │                      │
│  - Mensalidades      │     AlunoResponse         │  - Alunos            │
│  - Pagamentos        │                           │  - Funcionários      │
└──────────────────────┘                           └──────────────────────┘
```

---

## 📐 Decisões Arquiteturais

### **Por que Clean Architecture?**

1. **Independência de Frameworks** - Lógica de negócio não depende de Spring, JPA, etc
2. **Testabilidade** - Domain pode ser testado sem banco ou frameworks
3. **Flexibilidade** - Trocar banco/framework sem afetar regras de negócio
4. **Manutenibilidade** - Separação clara de responsabilidades

### **Por que Gateway Pattern para comunicação?**

```java
// Interface no domínio (não conhece HTTP)
public interface AlunoGateway {
    Optional<AlunoData> buscarPorId(Long id);
}

// Implementação na infraestrutura (usa Feign)
public class AlunoGatewayAdapter implements AlunoGateway {
    private final AlunoClient feignClient;
    // ...
}
```

Vantagens:
- ✅ Fácil trocar HTTP por RabbitMQ/Kafka/gRPC
- ✅ Fácil mockar em testes
- ✅ Domain não conhece detalhes de comunicação

---

## 🔐 Segurança

- Autenticação via JWT (compartilhado com microserviço principal)
- Filtro por usuário logado (não vê dados de outros usuários)
- Validação de entrada com Bean Validation

---

## 🚀 Publicação da imagem no ECR

### 1) Login no ECR

```bash
AWS_REGION=us-east-1
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
ECR_REGISTRY="$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com"

aws ecr get-login-password --region "$AWS_REGION" \
  | docker login --username AWS --password-stdin "$ECR_REGISTRY"
```

### 2) Build e push da imagem do Financial API

```bash
TAG=latest
AWS_REGION=us-east-1
AWS_ACCOUNT_ID=$(aws sts get-caller-identity --query Account --output text)
ECR_REGISTRY="$AWS_ACCOUNT_ID.dkr.ecr.$AWS_REGION.amazonaws.com"

docker build -t "$ECR_REGISTRY/safeway-financial:$TAG" .
docker push "$ECR_REGISTRY/safeway-financial:$TAG"
```

### 3) Atualizar rollout na infra

No projeto `safeway-infra`, confirme a imagem em `terraform.tfvars`:

```hcl
financial_api_image = "<account>.dkr.ecr.us-east-1.amazonaws.com/safeway-financial:latest"
```

Depois aplique:

```bash
cd ../safeway-infra
terraform apply
```

---