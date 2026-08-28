package com.felipefreitas.FourBank.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class TransferenciaRabbitEventListener {

    private final TransferenciaEventPublisher publisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publicar(TransferenciaConcluidaEvent event) {
        publisher.publicar(event);
    }
}
