package com.felipefreitas.FourBank.service;

import com.felipefreitas.FourBank.dto.auth.AuthTokenResponseDTO;
import com.felipefreitas.FourBank.dto.auth.LoginRequestDTO;
import com.felipefreitas.FourBank.security.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthTokenResponseDTO authenticate(LoginRequestDTO request) {
        log.info("Iniciando autenticação para login={}", request.login());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.login(), request.senha())
        );

        UserDetails principal = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(principal);
        log.info("Autenticação realizada com sucesso para login={}", principal.getUsername());

        return new AuthTokenResponseDTO("Bearer", token, jwtService.getExpirationMillis());
    }
}
