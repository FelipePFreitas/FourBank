package com.felipefreitas.FourBank.dto.cliente;

import java.util.UUID;

public record ClientePFResponseDTO(
        UUID id,
        String nome,
        String dataNascimento

) {
}
