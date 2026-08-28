package com.felipefreitas.FourBank.messaging;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferenciaConcluidaEvent(
        UUID messageId,
        UUID transacaoId,
        UUID contaOrigemId,
        UUID contaDestinoId,
        BigDecimal valor,
        LocalDateTime ocorridoEm
) {
}
