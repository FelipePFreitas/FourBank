package com.felipefreitas.FourBank.entity;

import com.felipefreitas.FourBank.enums.StatusCliente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "clientes_pj")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClientePJEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String cnpj;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String telefone;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_cliente", nullable = false)
    private StatusCliente statusCliente;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id", nullable = false)
    private EnderecosEntity endereco;

    @Column(nullable = false)
    private String razaoSocial;

    private String nomeFantasia;

    private LocalDate dataFundacao;

    @Column(nullable = false)
    private BigDecimal faturamentoAnual;

}
