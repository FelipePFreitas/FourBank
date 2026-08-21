package com.felipefreitas.FourBank.controller;

import com.felipefreitas.FourBank.dto.auth.AuthTokenResponseDTO;
import com.felipefreitas.FourBank.dto.auth.LoginRequestDTO;
import com.felipefreitas.FourBank.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponseDTO> login(@RequestBody @Valid LoginRequestDTO request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}
