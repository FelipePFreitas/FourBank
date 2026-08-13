package com.felipefreitas.FourBank.domain.util;

import java.util.regex.Pattern;

public final class CpfCnpjValidatorUtils {

    // Regex que aceita tanto o CNPJ formatado quanto o desformatado (Aceitando Letras nas posições corretas)
    private static final Pattern CNPJ_ALFANUMERICO_PATTERN = Pattern.compile("^[a-zA-Z0-9]{14}$|^[a-zA-Z0-9]{2}\\.[a-zA-Z0-9]{3}\\.[a-zA-Z0-9]{3}/[a-zA-Z0-9]{4}-[0-9]{2}$");
    private static final Pattern CPF_PATTERN = Pattern.compile("^\\d{11}$|^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$");

    private CpfCnpjValidatorUtils() {
        // Construtor privado para evitar instanciação de classe utilitária
    }

    /**
     * Valida um CNPJ seguindo as regras do novo padrão Alfanumérico (CNPJ Plural).
     */
    public static boolean isValidCnpj(String cnpj) {
        if (cnpj == null) return false;

        // 1. Valida o formato inicial por Regex
        if (!CNPJ_ALFANUMERICO_PATTERN.matcher(cnpj).matches()) {
            return false;
        }

        // 2. Limpa a formatação para trabalhar com os 14 caracteres puros
        String cleanCnpj = cnpj.replaceAll("[.\\-/]", "").toUpperCase();

        // 3. Validação dos Dígitos Verificadores (Cálculo adaptado para a tabela ASCII)
        try {
            // Primeiro dígito verificador
            int peso = 5;
            int soma = 0;
            for (int i = 0; i < 12; i++) {
                soma += converterCaractereParaValor(cleanCnpj.charAt(i)) * peso;
                peso = (peso == 2) ? 9 : peso - 1;
            }
            int dv1 = 11 - (soma % 11);
            if (dv1 >= 10) dv1 = 0;

            // Segundo dígito verificador
            peso = 6;
            soma = 0;
            for (int i = 0; i < 13; i++) {
                soma += converterCaractereParaValor(cleanCnpj.charAt(i)) * peso;
                peso = (peso == 2) ? 9 : peso - 1;
            }
            int dv2 = 11 - (soma % 11);
            if (dv2 >= 10) dv2 = 0;

            // Compara os DVs calculados com os DVs reais da String
            return (dv1 == Character.getNumericValue(cleanCnpj.charAt(12)) &&
                    dv2 == Character.getNumericValue(cleanCnpj.charAt(13)));

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Auxiliar para o CNPJ Alfanumérico: Converte a posição ASCII do caractere
     * subtraindo 48 (conforme a regra oficial da Receita Federal).
     */
    private static int converterCaractereParaValor(char c) {
        // Na tabela ASCII, '0' é 48. Subtraindo 48, '0' vira 0, 'A' (65) vira 17, etc.
        int valor = (int) c - 48;
        if (valor < 0 || valor > 42) { // Limite máximo para a letra 'Z'
            throw new IllegalArgumentException("Caractere inválido no CNPJ");
        }
        return valor;
    }

    /**
     * Valida um CPF tradicional (Apenas numérico).
     */
    public static boolean isValidCpf(String cpf) {
        if (cpf == null) return false;

        if (!CPF_PATTERN.matcher(cpf).matches()) {
            return false;
        }

        String cleanCpf = cpf.replaceAll("[.\\-]", "");

        // Elimina CPFs com todos os números iguais conhecidos
        if (cleanCpf.length() != 11 || cleanCpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            // Validação do primeiro DV
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += Character.getNumericValue(cleanCpf.charAt(i)) * (10 - i);
            }
            int dv1 = 11 - (soma % 11);
            if (dv1 >= 10) dv1 = 0;

            // Validação do segundo DV
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += Character.getNumericValue(cleanCpf.charAt(i)) * (11 - i);
            }
            int dv2 = 11 - (soma % 11);
            if (dv2 >= 10) dv2 = 0;

            return (dv1 == Character.getNumericValue(cleanCpf.charAt(9)) &&
                    dv2 == Character.getNumericValue(cleanCpf.charAt(10)));

        } catch (Exception e) {
            return false;
        }
    }
}