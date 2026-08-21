package com.felipefreitas.FourBank.enums;

import lombok.Getter;

@Getter
public enum ErrorEnum {
    // Erros de Cliente (faixa 1-99)
    CPF_INVALIDO(400, 1, "CPF inválido"),
    CNPJ_INVALIDO(400, 2, "CNPJ inválido"),
    CLIENTE_JA_CADASTRADO(409, 3, "Cliente já cadastrado"),
    CARACTERES_ACIMA(400, 4, "Limite de caracteres excedido"),
    NULO_BRANCO(400, 5, "Campo obrigatório não pode ser nulo ou em branco"),
    CPF_NULO_BRANCO(400, 6, "CPF não pode ser nulo ou em branco"),
    DATA_NASCIMENTO_NULO_BRANCO(400, 7, "Data de nascimento não pode ser nula ou em branco"),
    CEP_INVALIDO(400, 8, "CEP inválido"),
    TIPO_CLIENTE_INVALIDO(400, 9, "Tipo de cliente inválido"),
    CNPJ_NULO_BRANCO(400, 10, "CNPJ não pode ser nulo ou em branco"),
    CPF_JA_CADASTRADO(409, 204, "CPF já cadastrado"),
    LOGIN_JA_CADASTRADO(409, 205, "Login já cadastrado"),

    // Erros de Conta (faixa 100-199)
    SALDO_NEGATIVO_NULO(400, 100, "Saldo não pode ser null ou menor que zero"),
    NUMERO_CONTA_NAO_EXISTE(404, 101, "Conta não encontrada"),
    SALDO_INSUFICIENTE(422, 102, "Saldo insuficiente para transferência"),
    LIMITE_CHAVEPIX(422, 103, "Limite máximo de chaves Pix atingido"),
    CHAVEPIX_JACADASTRADA(409, 104, "Chave Pix já cadastrada para esta conta"),
    CHAVEPIX_INEXISTENTE(404, 105, "Chave Pix não encontrada"),


    // Erros de Transação (faixa 200+)
    SAQUE_NULO_ZERO(400, 200, "Valor do saque deve ser maior que zero"),
    SAQUE_VALOR_MAIOR_SALDO(422, 201, "Valor do saque maior que o saldo disponível"),
    DEPOSITO_NULO_ZERO(400, 202, "Valor do depósito deve ser maior que zero"),
    TIPO_TRANSACAO_INEXISTENTE(400, 203, "Tipo de transação inválido");

    private final int httpStatus;
    private final int errorCode;
    private final String errorMessage;

    ErrorEnum(int httpStatus, int errorCode, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}