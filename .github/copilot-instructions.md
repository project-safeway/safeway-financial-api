# Copilot Instructions - Safeway Financial API

Objetivo
- Microservico financeiro do Safeway (mensalidades, pagamentos, rotinas mensais).

Arquitetura e stack
- Clean Architecture (domain, application, infrastructure, presentation).
- Java 21, Spring Boot 3.
- JPA + Flyway.
- Mensageria com RabbitMQ.
- Integracao com servico core via OpenFeign.

Padroes de codigo
- UseCase interface + UseCaseImpl.
- Ports (input/output) no application; adapters no infrastructure.
- Entidades no domain sem dependencias de Spring.
- Specifications para filtros dinamicos.
- DTOs separados por camada (presentation vs application).

Confiabilidades e riscos comuns
- Mapeamento entre DTOs pode crescer: manter mappers organizados.
- Garantir mapeamento consistente de excecoes para HTTP.
- Validar performance de Specifications em consultas grandes.

Comandos usuais
- ./mvnw clean package
- ./mvnw test
- ./mvnw validate
- ./mvnw spring-boot:run

Boas praticas de mudanca
- Respeitar dependencias da Clean Architecture.
- Criar ports/gateways antes de adapters.
- Preservar padrao de nomes (UseCase, Gateway).

Sugestoes de prompts
- "Como adicionar um novo caso de uso seguindo Clean Architecture?"
- "Aponte riscos de performance nas consultas com filtros."
- "Proponha melhorias no scheduler de mensalidades."
