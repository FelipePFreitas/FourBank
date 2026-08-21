package com.felipefreitas.FourBank.dto.cliente;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ClientePJRequestDTO(
        @NotBlank(message = "Razão social é obrigatória")
        String razaoSocial,

        String nomeFantasia,

        LocalDate dataFundacao,

        @NotNull(message = "Faturamento anual é obrigatório")
        @PositiveOrZero(message = "Faturamento anual não pode ser negativo")
        BigDecimal faturamentoAnual,

        @Valid
        @NotNull(message = "Dados base do cliente são obrigatórios")
        ClienteRequestDTO cliente
) {
}
