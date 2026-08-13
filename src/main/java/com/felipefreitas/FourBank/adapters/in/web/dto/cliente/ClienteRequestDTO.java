package com.felipefreitas.FourBank.adapters.in.web.dto.cliente;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteRequestDTO(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "Email é obrigatório")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "A senha deve conter no mínimo 6 caracteres ")
        String senha,

        @NotBlank(message = "Documento é obrigatório")
        String documento,

        @NotBlank(message = "Endereço obrigatório")
        String endereco,

        @NotBlank(message = "numero obrigatório")
        String numero,

        @NotBlank(message = "Cep obrigatório")
        @Size(min = 8)
        String cep,

        @NotBlank(message = "Bairro obrigatório")
        String bairro,

        @NotBlank(message = "Cidade obrigatório")
        String cidade,

        @NotBlank(message = "Estado obrigatório")
        @Size(max = 2)
        String estado

) {
}
