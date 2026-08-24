package com.felipefreitas.FourBank.repository;

import com.felipefreitas.FourBank.entity.ClientePFEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface ClientePFRepository extends JpaRepository<ClientePFEntity, UUID> {

    boolean existsByDocumento(String documento);
    boolean existsByEmail(String email);
}
