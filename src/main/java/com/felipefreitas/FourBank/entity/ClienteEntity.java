package com.felipefreitas.FourBank.entity;

import com.felipefreitas.FourBank.enums.ClienteTipo;
import com.felipefreitas.FourBank.enums.StatusCliente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "clientes")
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nomeRazaoSocial;

    @Column(nullable = false, unique = true, length = 100)
    private String documento;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, unique = true, length = 20)
    private String telefone;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private LocalDateTime atualizadoEm;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_cliente", nullable = false)
    private StatusCliente statusCliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cliente", nullable = false)
    private ClienteTipo clienteTipo;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id", nullable = false)
    private EnderecosEntity endereco;

    @OneToOne(mappedBy = "cliente")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private UsuarioEntity usuario;

}
