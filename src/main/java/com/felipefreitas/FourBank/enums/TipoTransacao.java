package com.felipefreitas.FourBank.enums;

import lombok.Getter;

@Getter
public enum TipoTransacao {
    DEPOSITO("Deposito"),
    SAQUE("Saque"),
    TRANSFERENCIA("Transferência"),
    PIX("Pix");

    private final String descricao;

    TipoTransacao(String descricao) {
        this.descricao = descricao;
    }
}
