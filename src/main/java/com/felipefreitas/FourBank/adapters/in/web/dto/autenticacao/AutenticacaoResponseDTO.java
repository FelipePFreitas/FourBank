package com.felipefreitas.FourBank.adapters.in.web.dto.autenticacao;

public record AutenticacaoResponseDTO(String token,
                                      String tipo,
                                      Long expiracaoMs) {
}
