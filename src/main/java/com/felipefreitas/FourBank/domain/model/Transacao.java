package com.felipefreitas.FourBank.domain.model;

import com.felipefreitas.FourBank.domain.enums.TipoTransacao;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transacao {

    private UUID id;
    private BigDecimal valor;
    private TipoTransacao tipoTransacao;
    private LocalDateTime dataHora;
    private Conta contaOrigem;
    private Conta contaDestino;
}
