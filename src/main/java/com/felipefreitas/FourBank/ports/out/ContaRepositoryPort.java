package com.felipefreitas.FourBank.ports.out;

import com.felipefreitas.FourBank.domain.model.Conta;

import java.util.Optional;

public interface ContaRepositoryPort {

    Conta save(Conta conta);

    boolean existsByNumeroConta(String numeroConta);

    Optional<Conta> findByNumeroConta (String numeroConta);

}
