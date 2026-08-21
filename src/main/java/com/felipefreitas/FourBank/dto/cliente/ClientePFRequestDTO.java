package com.felipefreitas.FourBank.dto.cliente;

import com.felipefreitas.FourBank.dto.endereco.EnderecoRequestDTO;
import com.felipefreitas.FourBank.dto.usuario.UsuarioRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClientePFRequestDTO(
        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "Data de nascimento é obrigatória")
        String dataNascimento,

        @NotBlank(message = "CPF é obrigatório")
        String cpf,

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
