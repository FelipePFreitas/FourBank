package com.felipefreitas.FourBank.messaging;

import com.felipefreitas.FourBank.config.RabbitMqConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransferenciaEventConsumer {

    private static final String IDEMPOTENCY_PREFIX = "rabbit:transferencia:";

    private final StringRedisTemplate redisTemplate;

    @RabbitListener(queues = RabbitMqConfig.QUEUE)
    public void consumir(TransferenciaConcluidaEvent event) {
        String key = IDEMPOTENCY_PREFIX + event.messageId();
        Boolean firstDelivery = redisTemplate.opsForValue().setIfAbsent(key, "1", Duration.ofHours(24));
        if (!Boolean.TRUE.equals(firstDelivery)) {
            log.info("Evento de transferência duplicado ignorado. messageId={}", event.messageId());
            return;
        }

        log.info("Evento de transferência consumido. messageId={} transacaoId={} origemId={} destinoId={}",
                event.messageId(), event.transacaoId(), event.contaOrigemId(), event.contaDestinoId());
    }
}
