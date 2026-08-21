package com.felipefreitas.FourBank.dto.endereco;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EnderecoRequestDTO(
        @NotBlank(message = "Endereço é obrigatório")
        String endereco,

        @NotBlank(message = "Número é obrigatório")
        String numero,

        @NotBlank(message = "CEP é obrigatório")
        String cep,

        @NotBlank(message = "Bairro é obrigatório")
        String bairro,

        @NotBlank(message = "Cidade é obrigatória")
        String cidade,

        @NotBlank(message = "UF é obrigatória")
        @Size(min = 2, max = 2, message = "UF deve conter 2 caracteres")
        String uf
) {
}
