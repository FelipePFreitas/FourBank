package com.felipefreitas.FourBank.adapters.out.persistence;

import com.felipefreitas.FourBank.adapters.out.persistence.entity.ContaEntity;
import com.felipefreitas.FourBank.adapters.out.persistence.entity.TransacaoEntity;
import com.felipefreitas.FourBank.adapters.out.persistence.mapper.TransacaoMapper;
import com.felipefreitas.FourBank.adapters.out.persistence.repository.TransacaoRepository;
import com.felipefreitas.FourBank.domain.model.Transacao;
import com.felipefreitas.FourBank.ports.out.TransacaoPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TransacaoAdapter implements TransacaoPort {

    private final TransacaoRepository transacaoRepository;
    private final TransacaoMapper transacaoMapper;

    @Override
    public Transacao save(Transacao transacao) {
        TransacaoEntity transacaoEntity = transacaoMapper.toEntity(transacao);
        TransacaoEntity transacaoSalva = transacaoRepository.save(transacaoEntity);
        return transacaoMapper.toDomain(transacaoSalva);
    }
}
