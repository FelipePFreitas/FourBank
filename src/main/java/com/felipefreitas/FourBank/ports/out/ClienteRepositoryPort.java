package com.felipefreitas.FourBank.ports.out;

import com.felipefreitas.FourBank.domain.model.Cliente;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteRepositoryPort {

    Cliente save(Cliente cliente);

    Optional<Cliente> findById (UUID id);

    List<Cliente> findAll ();

    void deleteById(UUID id);

    Optional<Cliente> findByEmail (String email);


}
