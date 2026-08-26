package com.felipefreitas.FourBank.dto.transacao;

import com.felipefreitas.FourBank.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransacaoResponseDTO(
        UUID id,
        TipoTransacao tipoTransacao,
        BigDecimal valor,
        String descricao,
        LocalDateTime criadoEm,
        UUID contaOrigemId,
        UUID contaDestinoId
) {
}
