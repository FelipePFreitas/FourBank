package com.felipefreitas.FourBank.repository;

import com.felipefreitas.FourBank.entity.ContaEntity;
import com.felipefreitas.FourBank.entity.TransacaoEntity;
import com.felipefreitas.FourBank.enums.StatusTransacao;
import com.felipefreitas.FourBank.enums.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransacaoRepository extends JpaRepository<TransacaoEntity, UUID> {

    long countByContaOrigemAndTipoTransacaoAndStatusTransacaoAndCriadoEmBetween(
            ContaEntity contaOrigem,
            TipoTransacao tipoTransacao,
            StatusTransacao statusTransacao,
            LocalDateTime inicio,
            LocalDateTime fim);

    List<TransacaoEntity> findByStatusTransacaoAndAgendadaParaLessThanEqual(
            StatusTransacao statusTransacao,
            LocalDateTime limite);
}
