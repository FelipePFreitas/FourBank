package com.felipefreitas.FourBank.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "usuarios")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false,unique = true)
    private String login;

    @Column(nullable = false)
    private String senha;

    @OneToOne
    @JoinColumn(name = "cliente_pf_id", referencedColumnName = "id", unique = true)
    private ClientePFEntity clientePF;

    @OneToOne
    @JoinColumn(name = "cliente_pj_id", referencedColumnName = "id", unique = true)
    private ClientePJEntity clientePJ;

}
