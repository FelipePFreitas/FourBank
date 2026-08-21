package com.felipefreitas.FourBank.dto.auth;

public record AuthTokenResponseDTO(
        String tokenType,
        String accessToken,
        long expiresInMillis
) {
}
