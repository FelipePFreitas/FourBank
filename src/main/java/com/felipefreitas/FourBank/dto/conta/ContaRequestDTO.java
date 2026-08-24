package com.felipefreitas.FourBank.dto.conta;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

public record ContaRequestDTO(
        @NotNull(message = "Saldo inicial é obrigatório")
        @PositiveOrZero(message = "Saldo não pode ser negativo")
        BigDecimal saldoInicial,

        @NotNull(message = "ID do cliente é obrigatório")
        UUID clienteId
) {
}
