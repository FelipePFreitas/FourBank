package com.felipefreitas.FourBank.repository;

import com.felipefreitas.FourBank.entity.ContaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface ContaRepository extends JpaRepository<ContaEntity, UUID> {
}
