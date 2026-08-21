package com.felipefreitas.FourBank.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "cliente_pj")
@PrimaryKeyJoinColumn(name = "cliente_id_pj")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
public class ClientePJEntity extends ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String razaoSocial;

    private String nomeFantasia;

    private LocalDate dataFundacao;

    @Column(nullable = false)
    private BigDecimal faturamentoAnual;

}
