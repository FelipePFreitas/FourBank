package com.felipefreitas.FourBank.dto.cliente;

import com.felipefreitas.FourBank.dto.endereco.EnderecoResponseDTO;
import com.felipefreitas.FourBank.enums.ClienteTipo;
import com.felipefreitas.FourBank.enums.StatusCliente;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClienteResponseDTO(
        UUID id,
        String documento,
        String email,
        String telefone,
        ClienteTipo clienteTipo,
        StatusCliente statusCliente,
        LocalDateTime criadoEm,
        LocalDateTime atualizadoEm,
        EnderecoResponseDTO endereco
) {
}
