package com.felipefreitas.FourBank.adapters.out.persistence.repository;

import com.felipefreitas.FourBank.adapters.out.persistence.entity.TransacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransacaoRepository extends JpaRepository<TransacaoEntity, UUID> {
}
