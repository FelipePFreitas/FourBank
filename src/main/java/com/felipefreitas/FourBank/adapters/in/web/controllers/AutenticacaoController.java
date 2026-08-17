package com.felipefreitas.FourBank.adapters.in.web.controllers;

import com.felipefreitas.FourBank.adapters.in.web.dto.autenticacao.AutenticacaoRequestDTO;
import com.felipefreitas.FourBank.adapters.in.web.dto.autenticacao.AutenticacaoResponseDTO;
import com.felipefreitas.FourBank.domain.service.AutenticacaoService;
import com.felipefreitas.FourBank.ports.in.autenticacao.AutenticarLoginUsecase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
@Tag(name = "Endpoint login", description = "Endpoint para validação de login e gerar token jwt")
public class AutenticacaoController {

    private final AutenticarLoginUsecase autenticarLogin;

    @PostMapping
    @Operation(summary = "Autenticar usuário", description = "Valida o e-mail e senha do cliente e retorna o token JWT de acesso.")    public ResponseEntity<AutenticacaoResponseDTO> login(@Valid @RequestBody AutenticacaoRequestDTO autenticacaoRequestDTO) {
        AutenticacaoResponseDTO autenticacaoResponseDTO = autenticarLogin.autenticarLogin(autenticacaoRequestDTO);
        return ResponseEntity.ok(autenticacaoResponseDTO);

    }

}


