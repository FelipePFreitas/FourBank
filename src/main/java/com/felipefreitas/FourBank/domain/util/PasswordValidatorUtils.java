package com.felipefreitas.FourBank.domain.util;

import java.util.regex.Pattern;

public final class PasswordValidatorUtils {

    // A sua regex: aceita letras, números e os caracteres @, $, %, & (entre 8 e 32 caracteres)
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9@$%&]{6,32}$");

    private PasswordValidatorUtils() {
        // Construtor privado para impedir instanciação de classe utilitária
    }

    /**
     * Valida se a senha atende aos critérios de comprimento,
     * letras, números e caracteres especiais permitidos.
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.isBlank()) {
            return false;
        }

        return PASSWORD_PATTERN.matcher(password).matches();
    }
}