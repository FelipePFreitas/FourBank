package com.felipefreitas.FourBank.ports.in.cliente;

import com.felipefreitas.FourBank.domain.model.Cliente;

import java.util.List;

public interface ListarTodosOsClienteUseCase {

    List<Cliente> listarTodosOsClientes();

}
