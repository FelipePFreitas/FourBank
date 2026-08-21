package com.felipefreitas.FourBank.dto.usuario;

import com.felipefreitas.FourBank.dto.cliente.ClienteResponseDTO;

import java.util.UUID;

public record UsuarioResponseDTO(
        UUID id,
        String login,
        ClienteResponseDTO cliente
) {
}
