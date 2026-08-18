package com.felipefreitas.FourBank.adapters.out.persistence.mapper;

import com.felipefreitas.FourBank.adapters.in.web.dto.cliente.ClienteRequestDTO;
import com.felipefreitas.FourBank.adapters.in.web.dto.cliente.ClienteResponseDTO;
import com.felipefreitas.FourBank.adapters.out.persistence.entity.ContaEntity;
import com.felipefreitas.FourBank.domain.model.Cliente;
import com.felipefreitas.FourBank.domain.model.Conta;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ContaMapper {

    Conta toDomain(ContaEntity contaEntity);

    ContaEntity toEntity(Conta conta);

    Conta toClienteDomain(ClienteRequestDTO dto);

    ClienteResponseDTO toClienteResponseDTO(Cliente domain);

}