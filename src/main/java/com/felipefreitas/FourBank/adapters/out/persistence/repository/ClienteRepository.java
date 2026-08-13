package com.felipefreitas.FourBank.adapters.out.persistence.repository;

import com.felipefreitas.FourBank.adapters.out.persistence.entity.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClienteRepository extends JpaRepository<ClienteEntity, UUID> {


}
