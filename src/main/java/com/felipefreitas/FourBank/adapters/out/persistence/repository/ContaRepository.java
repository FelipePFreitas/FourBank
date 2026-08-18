package com.felipefreitas.FourBank.adapters.out.persistence.repository;

import com.felipefreitas.FourBank.adapters.out.persistence.entity.ContaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContaRepository extends JpaRepository<ContaEntity, UUID> {

    boolean existsByNumeroConta (String numeroConta);

    Optional<ContaEntity> findByNumeroConta (String numeroConta);
}
