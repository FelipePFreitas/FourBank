package com.felipefreitas.FourBank.entity;

import com.felipefreitas.FourBank.enums.ClienteTipo;
import com.felipefreitas.FourBank.enums.StatusCliente;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "clientes")
@Inheritance(strategy = InheritanceType.JOINED)// Apenas a classe pai declara a tabela
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public abstract class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String documento;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String telefone;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    @Enumerated(EnumType.STRING)
    @Column(name = "cliente_tipo", nullable = false)
    private ClienteTipo clienteTipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_cliente", nullable = false)
    private StatusCliente statusCliente;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    private EnderecosEntity endereco;
}
