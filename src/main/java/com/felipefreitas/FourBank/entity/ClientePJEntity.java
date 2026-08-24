package com.felipefreitas.FourBank.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cliente_pj")
@PrimaryKeyJoinColumn(name = "cliente_id_pj") // Une a chave primária com a tabela mãe
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ClientePJEntity extends ClienteEntity {

    private String nomeFantasia;

    private LocalDate dataFundacao;

    @Column(nullable = false)
    private BigDecimal faturamentoAnual;

}
