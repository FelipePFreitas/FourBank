package com.felipefreitas.FourBank.dto.conta;

import java.math.BigDecimal;
import java.util.UUID;

public record ContaResponseDTO(
        UUID id,
        String agencia,
        String numeroConta,
        BigDecimal saldo,
        UUID clienteId
) {
}
