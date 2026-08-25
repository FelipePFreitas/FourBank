package com.felipefreitas.FourBank.repository;

import com.felipefreitas.FourBank.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, UUID> {
    Optional<UsuarioEntity> findByLogin(String login);

}
