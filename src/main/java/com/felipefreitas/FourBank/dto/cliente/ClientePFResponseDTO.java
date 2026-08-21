package com.felipefreitas.FourBank.dto.cliente;

import com.felipefreitas.FourBank.dto.endereco.EnderecoResponseDTO;
import com.felipefreitas.FourBank.dto.usuario.UsuarioResponseDTO;
import com.felipefreitas.FourBank.enums.StatusCliente;

import java.util.UUID;

public record ClientePFResponseDTO(
        UUID id,
        String nome,
        String dataNascimento,
        String cpf,
        String email,
        String telefone,
        StatusCliente statusCliente,
        EnderecoResponseDTO endereco,
        UsuarioResponseDTO usuario

) {
}
