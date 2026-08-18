package com.felipefreitas.FourBank.domain.util;

import java.util.concurrent.ThreadLocalRandom;

public final class ContaUtil {

    public static final String AGENCIA_PADRAO = "0001";

    private ContaUtil() {
        throw new UnsupportedOperationException("Classe utilitária não deve ser instanciada.");
    }

    public static String gerarNumeroConta() {
        // Gera um número aleatório entre 0 e 999999
        int numero = ThreadLocalRandom.current().nextInt(0, 10000000);

        // Converte o int diretamente para String
        return String.format("%07d", numero);
    }
}