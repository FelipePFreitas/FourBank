package com.felipefreitas.FourBank.adapters.out.persistence.entity;

import com.felipefreitas.FourBank.domain.enums.TipoTransacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transacoes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_transacao", nullable = false)
    private TipoTransacao tipoTransacao; // PIX, DEPOSITO, SAQUE, TRANSFERENCIA

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    // Conta de onde o dinheiro SAIU (ex: nula em caso de DEPÓSITO)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conta_origem")
    private ContaEntity contaOrigem;

    // Conta para onde o dinheiro FOI (ex: nula em caso de SAQUE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conta_destino")
    private ContaEntity contaDestino;
}