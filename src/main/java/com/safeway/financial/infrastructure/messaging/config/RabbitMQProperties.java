package com.safeway.financial.infrastructure.messaging.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter @Setter
@Configuration
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitMQProperties {

    private Exchanges exchanges = new Exchanges();
    private Queues queues = new Queues();
    private RoutingKeys routingKeys = new RoutingKeys();

    @Getter @Setter
    public static class Exchanges {
        private String aluno;
    }

    @Getter @Setter
    public static class Queues {
        private String alunoCriado;
        private String alunoAtualizado;
        private String alunoInativado;
    }

    @Getter @Setter
    public static class RoutingKeys {
        private String alunoCriado;
        private String alunoAtualizado;
        private String alunoInativado;
    }

}
