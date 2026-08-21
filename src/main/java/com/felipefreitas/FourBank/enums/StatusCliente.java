package com.felipefreitas.FourBank.enums;

import lombok.Getter;

@Getter
public enum StatusCliente {
    ATIVO("Ativo"),
    INATIVO("Inativo");

    private final String descricao;

    StatusCliente(String descricao) {
        this.descricao = descricao;
    }
}
