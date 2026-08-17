package com.felipefreitas.FourBank.domain.service;

import com.felipefreitas.FourBank.adapters.in.web.dto.autenticacao.AutenticacaoRequestDTO;
import com.felipefreitas.FourBank.adapters.in.web.dto.autenticacao.AutenticacaoResponseDTO;
import com.felipefreitas.FourBank.domain.enums.ErrorEnum;
import com.felipefreitas.FourBank.domain.exception.BaseException;
import com.felipefreitas.FourBank.domain.model.Cliente;
import com.felipefreitas.FourBank.ports.in.autenticacao.AutenticarLoginUsecase;
import com.felipefreitas.FourBank.ports.out.ClienteRepositoryPort;
import com.felipefreitas.FourBank.ports.out.JwtPort;
import com.felipefreitas.FourBank.ports.out.PasswordEncoderPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AutenticacaoService implements AutenticarLoginUsecase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final JwtPort jwtPort;

    @Override
    @Transactional(readOnly = true)
    public AutenticacaoResponseDTO autenticarLogin(AutenticacaoRequestDTO request) {

        // 1. Busca o cliente cadastrado
        Cliente cliente = clienteRepositoryPort.findByEmail(request.email())
                .orElseThrow(() -> new BaseException(ErrorEnum.CREDENCIAIS_INVALIDAS));

        // 2. Valida o hash da senha
        if (!passwordEncoderPort.matches(request.senha(), cliente.getSenha())) {
            throw new BaseException(ErrorEnum.CREDENCIAIS_INVALIDAS);
        }

        // 3. Gera o JWT a partir dos dados do cliente
        String token = jwtPort.gerarToken(cliente.getEmail());

        // 4. Retorna a resposta com o JWT
        return new AutenticacaoResponseDTO(token, "Bearer", 28800000L);
    }
}