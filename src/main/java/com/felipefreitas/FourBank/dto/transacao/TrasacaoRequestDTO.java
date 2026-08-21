package com.felipefreitas.FourBank.dto.transacao;

import com.felipefreitas.FourBank.enums.TipoTransacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TrasacaoRequestDTO(
        @NotNull(message = "Tipo da transação é obrigatório")
        TipoTransacao tipoTransacao,

        @NotBlank(message = "Valor é obrigatório")
        String valor,

        String descricao,

        @NotNull(message = "Conta de origem é obrigatória")
        UUID contaOrigemId,

        UUID contaDestinoId
) {
}
