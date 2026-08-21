package com.felipefreitas.FourBank.adapters.out.persistence.entity;


import com.felipefreitas.FourBank.domain.enums.TipoConta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public class ContaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 4)
    private String agencia;

    @Column(nullable = false, length = 7, unique = true)
    private String numeroConta;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal saldo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private ClienteEntity clienteEntity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoConta tipoConta;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "conta_chaves_pix",
            joinColumns = @JoinColumn(name = "id_conta"),
            uniqueConstraints = @UniqueConstraint(columnNames = "chave_pix") // 👈 Garante unicidade global da chave Pix no banco
    )
    @Column(name = "chave_pix", length = 77, nullable = false)
    private Set<String> chavesPix = new HashSet<>();


}
