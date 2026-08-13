package com.felipefreitas.FourBank.ports.in.cliente;

import com.felipefreitas.FourBank.domain.model.Cliente;

import java.util.UUID;

public interface BuscarClienteIdUseCase {

    Cliente buscarClientePorId(UUID id);
}
