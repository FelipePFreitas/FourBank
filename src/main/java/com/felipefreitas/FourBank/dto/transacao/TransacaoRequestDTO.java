package com.felipefreitas.FourBank.dto.transacao;

import com.felipefreitas.FourBank.enums.TipoTransacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransacaoRequestDTO(

        @NotNull(message = "Tipo da transação é obrigatório")
        TipoTransacao tipoTransacao,

        @NotBlank(message = "Valor é obrigatório")
        BigDecimal valor,

        String descricao,

        @NotNull(message = "Data de criação é obrigatória")
        LocalDateTime criadoEm,

        @NotNull(message = "Conta de origem é obrigatória")
        UUID contaOrigemId,

        UUID contaDestinoId
) {
}
