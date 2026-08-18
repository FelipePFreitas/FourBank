package com.felipefreitas.FourBank.domain.enums;

public enum TipoConta {
    PF("Pessoa Física"),
    PJ("Pessoa Jurídica");

    private String tipoConta;

    TipoConta(String tipoConta) {
        this.tipoConta = tipoConta;
    }
}
