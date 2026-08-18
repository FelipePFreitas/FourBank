package com.felipefreitas.FourBank.adapters.in.web.dto.conta;

import com.felipefreitas.FourBank.domain.enums.TipoConta;

import java.math.BigDecimal;
import java.util.UUID;

public record ContaResponseDTO(
        UUID id,
        String agencia,
        String numeroConta,
        BigDecimal saldo,
        TipoConta tipoConta

) {
}
