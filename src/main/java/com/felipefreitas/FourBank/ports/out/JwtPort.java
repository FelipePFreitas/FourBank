package com.felipefreitas.FourBank.ports.out;

public interface JwtPort {
    String gerarToken(String email);
}
