package com.felipefreitas.FourBank.entity;

import com.felipefreitas.FourBank.enums.StatusTransacao;
import com.felipefreitas.FourBank.enums.TipoTransacao;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transacoes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TransacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipoTransacao;

    @Column(nullable = false)
    private BigDecimal valor;

    private String descricao;

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    @ManyToOne
    @JoinColumn(name = "conta_origem_id", referencedColumnName = "id")
    private ContaEntity contaOrigem;

    @ManyToOne
    @JoinColumn(name = "conta_destino_id", referencedColumnName = "id")
    private ContaEntity contaDestino;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTransacao statusTransacao;


}