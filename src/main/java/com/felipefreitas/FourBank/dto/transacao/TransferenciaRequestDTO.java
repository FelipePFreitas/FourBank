package com.felipefreitas.FourBank.dto.transacao;

import com.felipefreitas.FourBank.enums.TipoConta;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferenciaRequestDTO(
        @NotBlank String nome,
        @NotBlank String documento,
        @NotBlank String banco,
        @NotBlank String agencia,
        @NotBlank String conta,
        @NotNull TipoConta tipoConta,
        @NotNull @DecimalMin(value = "0.01") BigDecimal valor,
        LocalDateTime agendadaPara
) {
}
