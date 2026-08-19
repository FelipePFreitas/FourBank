package com.felipefreitas.FourBank.domain.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Cliente {

    private UUID id;
    private String nome;
    private String email;
    private String senha;
    private String documento;
    private String endereco;
    private String numero;
    private String cep;
    private String bairro;
    private String cidade;
    private String estado;
    private boolean ativo;

}


