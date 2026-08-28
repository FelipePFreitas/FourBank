package com.felipefreitas.FourBank.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE = "fourbank.transferencias";
    public static final String QUEUE = "fourbank.transferencias.concluidas";
    public static final String ROUTING_KEY = "transferencia.concluida";

    @Bean
    public TopicExchange transferenciaExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue transferenciaQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Binding transferenciaBinding(Queue transferenciaQueue, TopicExchange transferenciaExchange) {
        return BindingBuilder.bind(transferenciaQueue)
                .to(transferenciaExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter rabbitMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
