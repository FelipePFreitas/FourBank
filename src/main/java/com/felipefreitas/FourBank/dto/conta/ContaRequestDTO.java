package com.felipefreitas.FourBank.dto.conta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

public record ContaRequestDTO(
        @NotBlank(message = "Agência é obrigatória")
        String agencia,

        @NotBlank(message = "Número da conta é obrigatório")
        String numeroConta,

        @NotNull(message = "Saldo é obrigatório")
        @PositiveOrZero(message = "Saldo não pode ser negativo")
        BigDecimal saldo,

        @NotNull(message = "ID do cliente é obrigatório")
        UUID clienteId
) {
}
