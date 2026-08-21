package com.felipefreitas.FourBank.adapters.out.persistence.mapper;

import com.felipefreitas.FourBank.adapters.in.web.dto.transacao.TransacaoResponseDTO;
import com.felipefreitas.FourBank.adapters.out.persistence.entity.TransacaoEntity;
import com.felipefreitas.FourBank.domain.model.Transacao;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransacaoMapper {

    Transacao toDomain(TransacaoEntity transacaoEntity);

    TransacaoEntity toEntity(Transacao transacao);

    TransacaoResponseDTO toResponse (Transacao transacao);
}
