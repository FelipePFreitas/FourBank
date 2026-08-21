package com.felipefreitas.FourBank.dto.cliente;

import com.felipefreitas.FourBank.dto.endereco.EnderecoResponseDTO;
import com.felipefreitas.FourBank.dto.usuario.UsuarioResponseDTO;
import com.felipefreitas.FourBank.enums.StatusCliente;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ClientePJResponseDTO(
        UUID id,
        String razaoSocial,
        String nomeFantasia,
        LocalDate dataFundacao,
        BigDecimal faturamentoAnual,
        String cnpj,
        String email,
        String telefone,
        StatusCliente statusCliente,
        EnderecoResponseDTO endereco,
        UsuarioResponseDTO usuario
) {
}
