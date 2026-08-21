package com.felipefreitas.FourBank.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private static final String ISSUER = "fourbank-api";

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final long expirationMillis;

    public JwtService(@Value("${api.security.token.secret}") String secret,
                      @Value("${api.security.token.expiration}") long expirationMillis) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(this.algorithm)
                .withIssuer(ISSUER)
                .build();
        this.expirationMillis = expirationMillis;
    }

    public String generateToken(UserDetails userDetails) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(expirationMillis);

        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(userDetails.getUsername())
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiration))
                .sign(algorithm);
    }

    public String extractUsername(String token) {
        return verifyToken(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            DecodedJWT decodedJWT = verifyToken(token);
            return decodedJWT.getSubject().equals(userDetails.getUsername()) &&
                    decodedJWT.getExpiresAtAsInstant().isAfter(Instant.now());
        } catch (JWTVerificationException ex) {
            return false;
        }
    }

    public long getExpirationMillis() {
        return expirationMillis;
    }

    private DecodedJWT verifyToken(String token) {
        return verifier.verify(token);
    }
}
