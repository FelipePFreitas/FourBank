package com.felipefreitas.FourBank.adapters.out.persistence.mapper;

import com.felipefreitas.FourBank.adapters.out.persistence.entity.ClienteEntity;
import com.felipefreitas.FourBank.domain.model.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClienteMapper {

    Cliente toClienteDomain(ClienteEntity clienteEntity);

    ClienteEntity toClienteEntity(Cliente cliente);

}
