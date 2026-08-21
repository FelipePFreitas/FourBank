package com.felipefreitas.FourBank.dto.endereco;

import java.util.UUID;

public record EnderecoResponseDTO(
        UUID id,
        String endereco,
        String numero,
        String cep,
        String bairro,
        String cidade,
        String uf
) {
}
