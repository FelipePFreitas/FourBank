package com.felipefreitas.FourBank.domain.enums;

public enum TipoTransacao {
    TRANSFERENCIA("Transferência"),
    SAQUE("Saque"),
    DEPOSITO("Depósito"),
    PIX("Pix");

    private final String descricao;

    TipoTransacao(String descricao) {
        this.descricao = descricao;
    }
}
