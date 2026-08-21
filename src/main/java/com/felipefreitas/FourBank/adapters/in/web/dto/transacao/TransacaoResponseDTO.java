package com.felipefreitas.FourBank.adapters.in.web.dto.transacao;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.felipefreitas.FourBank.adapters.in.web.dto.conta.ContaResponseDTO;
import com.felipefreitas.FourBank.domain.enums.TipoTransacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransacaoResponseDTO(
        UUID id,

        BigDecimal valor,
        TipoTransacao tipoTransacao,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime dataHora,

        ContaResponseDTO contaOrigem,
        ContaResponseDTO contaDestino


) {
}

