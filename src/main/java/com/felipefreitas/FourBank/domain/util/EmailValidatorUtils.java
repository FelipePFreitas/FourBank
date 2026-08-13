package com.felipefreitas.FourBank.domain.util;

import java.util.regex.Pattern;

public final class EmailValidatorUtils {

    // Regex robusta baseada na especificação oficial RFC 5322
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    private EmailValidatorUtils() {
        // Construtor privado para impedir instanciação de classe utilitária
    }

    /**
     * Valida se uma String possui um formato de e-mail válido.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }

        // Remove espaços em branco que o usuário possa ter digitado sem querer no início ou fim
        String trimmedEmail = email.trim();

        // Valida contra a expressão regular
        return EMAIL_PATTERN.matcher(trimmedEmail).matches();
    }
}