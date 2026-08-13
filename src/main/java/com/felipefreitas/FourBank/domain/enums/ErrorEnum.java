package com.felipefreitas.FourBank.domain.enums;

import lombok.Getter;

@Getter
public enum ErrorEnum {

    // 0-99: Erros Gerais, Entrada e Autenticação/Autorização (400, 401, 403, 404, 500)
    DADOS_INVALIDOS(400, 1, "Dados da requisição inválidos ou mal formatados"),
    NAO_AUTORIZADO(401, 2, "Token de acesso ausente, inválido ou expirado"),
    ACESSO_NEGADO(403, 3, "Você não tem permissão para acessar este recurso"),
    CREDENCIAIS_INVALIDAS(401, 4, "E-mail ou senha incorretos"),
    RECURSO_NAO_ENCONTRADO(404, 5, "Recurso solicitado não foi encontrado"),
    ERRO_INTERNO_SERVIDOR(500, 6, "Ocorreu um erro interno inesperado no servidor"),
    CPF_INVALIDO(400, 7, "O CPF informado é inválido"),
    CNPJ_INVALIDO(400, 8, "O CNPJ informado é inválido"),
    ACAO_NAO_PERMITIDA(400, 9, "Ação não permitida para o estado atual"),

    // 100-199: Erros de Cliente (Pessoa Física e Jurídica)
    CLIENTE_NAO_ENCONTRADO(404, 100, "Cliente não encontrado"),
    EMAIL_JA_CADASTRADO(409, 101, "O e-mail informado já está cadastrado para outro cliente"),
    CPF_JA_CADASTRADO(409, 102, "O CPF informado já está cadastrado no sistema"),
    CNPJ_JA_CADASTRADO(409, 103, "O CNPJ informado já está cadastrado no sistema"),
    DOCUMENTO_JA_CADASTRADO(409, 104, "O documento informado já está vinculado a uma conta"),
    CLIENTE_INATIVO(400, 105, "O cadastro do cliente encontra-se inativo ou bloqueado"),

    // 200-299: Erros de Conta Bancária
    CONTA_NAO_ENCONTRADA(404, 200, "Conta bancária não encontrada"),
    CONTA_JA_CADASTRADA(409, 201, "O cliente já possui uma conta deste tipo cadastrada"),
    CONTA_INATIVA(400, 202, "A conta bancária informada encontra-se inativa"),
    CONTA_BLOQUEADA(422, 203, "A conta bancária está bloqueada para movimentações"),
    SALDO_INSUFICIENTE(422, 204, "Saldo insuficiente para realizar a operação"),
    LIMITE_EXCEDIDO(422, 205, "O valor da operação excede o limite diário ou por transação"),

    // 300-399: Erros de Transações, PIX e Transferências
    TRANSACAO_NAO_ENCONTRADA(404, 300, "Transação não encontrada"),
    VALOR_INVALIDO(400, 301, "O valor da transação deve ser maior que zero"),
    CHAVE_PIX_NAO_ENCONTRADA(404, 302, "Chave PIX não encontrada"),
    CHAVE_PIX_JA_CADASTRADA(409, 303, "A chave PIX informada já pertence a outra conta"),
    TRANSACAO_MESMA_CONTA(422, 304, "Não é permitido realizar transferências para a própria conta origem"),
    TRANSACAO_JA_ESTORNADA(422, 305, "A transação selecionada já foi estornada anteriormente");

    private final int httpStatus;
    private final int errorCode;
    private final String errorMessage;

    ErrorEnum(int httpStatus, int errorCode, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}