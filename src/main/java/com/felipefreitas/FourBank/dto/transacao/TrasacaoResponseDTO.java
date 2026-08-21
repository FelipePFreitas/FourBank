package com.felipefreitas.FourBank.dto.transacao;

import com.felipefreitas.FourBank.enums.TipoTransacao;

import java.time.LocalDateTime;
import java.util.UUID;

public record TrasacaoResponseDTO(
        UUID id,
        TipoTransacao tipoTransacao,
        String valor,
        String descricao,
        LocalDateTime criadoEm,
        UUID contaOrigemId,
        UUID contaDestinoId
) {
}
