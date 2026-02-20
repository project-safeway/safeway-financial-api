package com.safeway.financial.infrastructure.messaging.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    private final RabbitMQProperties rabbitMQProperties;

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        return factory;
    }

    @Bean
    public TopicExchange alunoExchange() {
        return new TopicExchange(rabbitMQProperties.getExchanges().getAluno());
    }

    @Bean
    public Queue alunoCriadoDLQ() {
        return QueueBuilder.durable(rabbitMQProperties.getQueues().getAlunoCriado()).build();
    }

    @Bean
    public TopicExchange alunoDLX() {
        return new TopicExchange(rabbitMQProperties.getExchanges().getAluno() + ".dlx");
    }

    @Bean
    public Binding alunoCriadoDLQBinding() {
        return BindingBuilder
                .bind(alunoCriadoDLQ())
                .to(alunoDLX())
                .with("aluno.criado.dlq");
    }

    @Bean
    public Queue alunoCriadoQueue() {
        return QueueBuilder.durable(rabbitMQProperties.getQueues().getAlunoCriado())
                .withArgument("x-dead-letter-exchange", rabbitMQProperties.getExchanges().getAluno() + ".dlx")
                .withArgument("x-dead-letter-routing-key", "aluno.criado.dlq")
                .build();
    }

    @Bean
    public Queue alunoAtualizadoQueue() {
        return QueueBuilder.durable(rabbitMQProperties.getQueues().getAlunoAtualizado())
                .withArgument("x-dead-letter-exchange", rabbitMQProperties.getExchanges().getAluno() + ".dlx")
                .withArgument("x-dead-letter-routing-key", "aluno.atualizado.dlq")
                .build();
    }

    @Bean
    public Queue alunoInativadoQueue() {
        return QueueBuilder.durable(rabbitMQProperties.getQueues().getAlunoInativado())
                .withArgument("x-dead-letter-exchange", rabbitMQProperties.getExchanges().getAluno() + ".dlx")
                .withArgument("x-dead-letter-routing-key", "aluno.inativado.dlq")
                .build();
    }

    @Bean
    public Binding alunoCriadoBinding() {
        return BindingBuilder
                .bind(alunoCriadoQueue())
                .to(alunoExchange())
                .with(rabbitMQProperties.getRoutingKeys().getAlunoCriado());
    }

    @Bean
    public Binding alunoAtualizadoBinding() {
        return BindingBuilder
                .bind(alunoAtualizadoQueue())
                .to(alunoExchange())
                .with(rabbitMQProperties.getRoutingKeys().getAlunoAtualizado());
    }

    @Bean
    public Binding alunoInativadoBinding() {
        return BindingBuilder
                .bind(alunoInativadoQueue())
                .to(alunoExchange())
                .with(rabbitMQProperties.getRoutingKeys().getAlunoInativado());
    }

}
