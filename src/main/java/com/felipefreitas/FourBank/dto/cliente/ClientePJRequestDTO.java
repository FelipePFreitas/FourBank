package com.felipefreitas.FourBank.dto.cliente;

import com.felipefreitas.FourBank.dto.endereco.EnderecoRequestDTO;
import com.felipefreitas.FourBank.dto.usuario.UsuarioRequestDTO;
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

        @NotBlank(message = "CNPJ é obrigatório")
        String cnpj,

        @NotBlank(message = "E-mail é obrigatório")
        String email,

        @NotBlank(message = "Telefone é obrigatório")
        String telefone,

        @Valid
        @NotNull(message = "Endereço é obrigatório")
        EnderecoRequestDTO endereco,

        @Valid
        @NotNull(message = "Credenciais de usuário são obrigatórias")
        UsuarioRequestDTO usuario
) {
}
