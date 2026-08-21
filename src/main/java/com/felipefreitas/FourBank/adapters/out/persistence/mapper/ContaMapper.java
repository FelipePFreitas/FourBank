package com.felipefreitas.FourBank.adapters.out.persistence.mapper;

import com.felipefreitas.FourBank.adapters.out.persistence.entity.ContaEntity;
import com.felipefreitas.FourBank.domain.model.Conta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {ClienteMapper.class})
public interface ContaMapper {

    // Fonte é ContaEntity (tem 'clienteEntity') -> Target é Conta (tem 'cliente')
    @Mapping(target = "cliente", source = "clienteEntity")
    Conta toDomain(ContaEntity contaEntity);

    // Fonte é Conta (tem 'cliente') -> Target é ContaEntity (tem 'clienteEntity')
    @Mapping(target = "clienteEntity", source = "cliente")
    ContaEntity toEntity(Conta conta);

}