package com.felipefreitas.FourBank.dto.cliente;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ClientePJResponseDTO(
        UUID id,
        String razaoSocial,
        String nomeFantasia,
        LocalDate dataFundacao,
        BigDecimal faturamentoAnual,
        ClienteResponseDTO cliente
) {
}
