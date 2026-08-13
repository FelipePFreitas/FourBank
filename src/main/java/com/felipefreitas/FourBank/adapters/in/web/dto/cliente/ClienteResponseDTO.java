package com.felipefreitas.FourBank.adapters.in.web.dto.cliente;

public record ClienteResponseDTO(

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
