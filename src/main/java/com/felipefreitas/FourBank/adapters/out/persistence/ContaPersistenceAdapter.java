package com.felipefreitas.FourBank.adapters.out.persistence;


import com.felipefreitas.FourBank.adapters.out.persistence.entity.ContaEntity;
import com.felipefreitas.FourBank.adapters.out.persistence.mapper.ContaMapper;
import com.felipefreitas.FourBank.adapters.out.persistence.repository.ContaRepository;
import com.felipefreitas.FourBank.domain.model.Conta;
import com.felipefreitas.FourBank.ports.out.ContaPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class ContaPersistenceAdapter implements ContaPort {

    private final ContaRepository contaRepository;
    private final ContaMapper contaMapper;

    @Override
    public Conta save(Conta conta) {
        ContaEntity contaEntity = contaMapper.toEntity(conta);
        ContaEntity contaSalva = contaRepository.save(contaEntity);
        return contaMapper.toDomain(contaSalva);
    }

    @Override
    public boolean existsByNumeroConta(String conta) {
        return contaRepository.existsByNumeroConta(conta);
    }

    @Override
    public Optional<Conta> findByNumeroConta(String numeroConta) {
        return contaRepository.findByNumeroConta(numeroConta).map(contaMapper::toDomain);
    }

    @Override
    public Optional<Conta> findByChavePix(String chavePix) {
        return contaRepository.findByChavesPixContaining(chavePix).map(contaMapper::toDomain);
    }


}
