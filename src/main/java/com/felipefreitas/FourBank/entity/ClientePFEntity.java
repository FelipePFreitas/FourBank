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

@Entity
@Table(name = "cliente_pf")
@PrimaryKeyJoinColumn(name = "cliente_id_pf") // Une a chave primária com a tabela mãe
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ClientePFEntity extends ClienteEntity {

    @Column(nullable = false)
    private String dataNascimento;

}
