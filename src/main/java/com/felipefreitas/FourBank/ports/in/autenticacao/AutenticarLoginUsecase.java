package com.felipefreitas.FourBank.ports.in.autenticacao;

import com.felipefreitas.FourBank.adapters.in.web.dto.autenticacao.AutenticacaoRequestDTO;
import com.felipefreitas.FourBank.adapters.in.web.dto.autenticacao.AutenticacaoResponseDTO;

public interface AutenticarLoginUsecase {

    AutenticacaoResponseDTO autenticarLogin(AutenticacaoRequestDTO autenticacaoRequestDTO);
}
