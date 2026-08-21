package com.felipefreitas.FourBank.dto.usuario;

import java.util.UUID;

public record UsuarioResponseDTO(
        UUID id,
        String login,
        ClienteResponseDTO cliente
) {
}
