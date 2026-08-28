package com.felipefreitas.FourBank.messaging;

import com.felipefreitas.FourBank.config.RabbitMqConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransferenciaEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publicar(TransferenciaConcluidaEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE,
                RabbitMqConfig.ROUTING_KEY,
                event,
                message -> {
                    message.getMessageProperties().setMessageId(event.messageId().toString());
                    message.getMessageProperties().setContentType("application/json");
                    return message;
                });
        log.info("Evento de transferência publicado. messageId={} transacaoId={}",
                event.messageId(), event.transacaoId());
    }
}
