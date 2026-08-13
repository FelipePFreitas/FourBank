package com.felipefreitas.FourBank.adapters.out.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${api.security.token.secret}")
    private String secretKey;

    @Value("${api.security.token.expiration}")
    private Long expirationTime;

    // Gera a chave criptográfica a partir da Secret definida
    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Gerar o Token JWT contendo o e-mail do usuário como Subject
    public String gerarToken(String email) {
        Date agora = new Date();
        Date dataExpiracao = new Date(agora.getTime() + expirationTime);

        return Jwts.builder()
                .subject(email)
                .issuedAt(agora)
                .expiration(dataExpiracao)
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    // Extrai o e-mail (Subject) do Token
    public String extrairEmail(String token) {
        return extrairClaim(token, Claims::getSubject);
    }

    // Valida se o token pertence ao usuário e se não está expirado
    public boolean isTokenValido(String token, String emailUsuario) {
        final String emailNoToken = extrairEmail(token);
        return (emailNoToken.equals(emailUsuario) && !isTokenExpirado(token));
    }

    // Verifica se a data de expiração já passou
    private boolean isTokenExpirado(String token) {
        return extrairExpiracao(token).before(new Date());
    }

    private Date extrairExpiracao(String token) {
        return extrairClaim(token, Claims::getExpiration);
    }

    // Método genérico para extrair qualquer informação (Claim) do token
    public <T> T extrairClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extrairTodasClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extrairTodasClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}