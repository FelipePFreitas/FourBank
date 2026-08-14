package com.felipefreitas.FourBank.adapters.in.web.dto.cliente;

import java.util.UUID;

public record ClienteResponseDTO(

        UUID id,
        String nome,
        String email,
        String documento,
        String endereco,
        String numero,
        String cep,
        String bairro,
        String cidade,
        String estado
) {
}