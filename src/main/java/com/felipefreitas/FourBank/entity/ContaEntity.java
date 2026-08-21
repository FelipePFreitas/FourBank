package com.felipefreitas.FourBank.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "contas")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ContaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String agencia;

    @Column(nullable = false)
    private String numeroConta;

    @Column(nullable = false)
    private BigDecimal saldo;

    @OneToOne
    @JoinColumn(name = "cliente_pf_id", referencedColumnName = "id", unique = true)
    private ClientePFEntity clientePF;

    @OneToOne
    @JoinColumn(name = "cliente_pj_id", referencedColumnName = "id", unique = true)
    private ClientePJEntity clientePJ;

}
