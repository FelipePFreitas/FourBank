package com.felipefreitas.FourBank.dto.cliente;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClientePFRequestDTO(
        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "Data de nascimento é obrigatória")
        String dataNascimento
) {
}
