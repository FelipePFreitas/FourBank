package com.felipefreitas.FourBank.domain.service;


import com.felipefreitas.FourBank.adapters.in.web.dto.autenticacao.AutenticacaoRequestDTO;
import com.felipefreitas.FourBank.adapters.in.web.dto.autenticacao.AutenticacaoResponseDTO;
import com.felipefreitas.FourBank.domain.enums.ErrorEnum;
import com.felipefreitas.FourBank.domain.exception.BaseException;
import com.felipefreitas.FourBank.domain.model.Cliente;

import com.felipefreitas.FourBank.ports.in.autenticacao.AutenticarLoginUsecase;
import com.felipefreitas.FourBank.ports.out.ClienteRepositoryPort;
import com.felipefreitas.FourBank.ports.out.JwtPort;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@AllArgsConstructor
public class AutenticacaoService implements AutenticarLoginUsecase {

    private final PasswordEncoder passwordEncoder;
    private final ClienteRepositoryPort clienteRepositoryPort; // 👈 Usa a Porta em vez do Repository JPA
    private final JwtPort jwtPort;                           // 👈 Usa a Porta em vez do JwtService

    @Override
    public AutenticacaoResponseDTO autenticarLogin(AutenticacaoRequestDTO request) {

        // 1. Busca o cliente pelo Port e retorna o modelo de Domínio (Cliente)
        Cliente cliente = clienteRepositoryPort.findByEmail(request.email())
                .orElseThrow(() -> new BaseException(ErrorEnum.CREDENCIAIS_INVALIDAS));

        // 2. Valida se a senha enviada bate com o hash salvo no banco
        if (!passwordEncoder.matches(request.senha(), cliente.getSenha())) {
            throw new BaseException(ErrorEnum.CREDENCIAIS_INVALIDAS);
        }

        // 3. Gerar o token JWT utilizando a Porta de Saída
        String token = jwtPort.gerarToken(cliente.getEmail());

        // 4. Retorna a resposta formatada
        return new AutenticacaoResponseDTO(
                token,
                "Bearer",
                28800000L // 8 horas em milissegundos
        );
    }
}