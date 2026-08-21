package com.felipefreitas.FourBank.repository;

import com.felipefreitas.FourBank.entity.ClientePJEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface ClientePJRepository extends JpaRepository<ClientePJEntity, UUID> {
}
