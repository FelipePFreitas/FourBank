package com.felipefreitas.FourBank.adapters.in.web.dto.autenticacao;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AutenticacaoRequestDTO(
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email em formato inválido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        String senha) {}