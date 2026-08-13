package com.felipefreitas.FourBank.adapters.out.persistence.mapper;

import com.felipefreitas.FourBank.adapters.in.web.dto.cliente.ClienteRequestDTO;
import com.felipefreitas.FourBank.adapters.in.web.dto.cliente.ClienteResponseDTO;
import com.felipefreitas.FourBank.adapters.out.persistence.entity.ClienteEntity;
import com.felipefreitas.FourBank.domain.model.Cliente;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ClienteMapper {

    Cliente toDomain(ClienteEntity clienteEntity);

    ClienteEntity toEntity(Cliente cliente);

    Cliente toClienteDomain(ClienteRequestDTO dto);

    ClienteResponseDTO toClienteResponseDTO(Cliente domain);

}
