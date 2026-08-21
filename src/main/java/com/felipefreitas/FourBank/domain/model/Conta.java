package com.felipefreitas.FourBank.domain.model;

import com.felipefreitas.FourBank.domain.enums.TipoConta;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
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
    private Set<String> chavesPix = new HashSet<>();


}
