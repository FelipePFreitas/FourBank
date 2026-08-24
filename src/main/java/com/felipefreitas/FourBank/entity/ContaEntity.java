package com.felipefreitas.FourBank.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "contas")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ContaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String agencia;

    @Column(nullable = false, unique = true)
    private String numeroConta;

    @Column(nullable = false)
    private BigDecimal saldo;

    @OneToOne
    @JoinColumn(name = "cliente_id", referencedColumnName = "id", unique = true, nullable = false)
    private ClienteEntity cliente;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "conta_chaves_pix",
            joinColumns = @JoinColumn(name = "conta_id") // Aponta para a tabela de contas
    )
    @Column(name = "chave_pix", length = 77)
    @Builder.Default
    private Set<String> chavesPix = new HashSet<>();

}
