package com.felipefreitas.FourBank.domain.model;

import com.felipefreitas.FourBank.domain.enums.TipoConta;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Conta {

    private UUID id;
    private String agencia;
    private String numeroConta;
    private BigDecimal saldo;
    private Cliente cliente;
    private TipoConta tipoConta;

}
