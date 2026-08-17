package com.felipefreitas.FourBank.adapters.out.persistence;

import com.felipefreitas.FourBank.ports.out.JwtPort;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component // 🏆 ESTA ANOTAÇÃO RESOLVE O ERRO DO SPRING
public class JwtAdapter implements JwtPort {

    @Value("${jwt.secret:fourbank_secret_key_super_segura_1234567890_abc}")
    private String secretKey;

    @Value("${jwt.expiration:28800000}") // 8 horas por padrão
    private Long expiration;

    @Override
    public String gerarToken(String email) {
        Date agora = new Date();
        Date dataExpiracao = new Date(agora.getTime() + expiration);
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(agora)
                .setExpiration(dataExpiracao)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}