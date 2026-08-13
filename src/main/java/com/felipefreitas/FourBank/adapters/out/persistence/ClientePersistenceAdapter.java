package com.felipefreitas.FourBank.adapters.out.persistence;

import com.felipefreitas.FourBank.adapters.out.persistence.entity.ClienteEntity;
import com.felipefreitas.FourBank.adapters.out.persistence.mapper.ClienteMapper;
import com.felipefreitas.FourBank.adapters.out.persistence.repository.ClienteRepository;
import com.felipefreitas.FourBank.domain.model.Cliente;
import com.felipefreitas.FourBank.ports.out.ClienteRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ClientePersistenceAdapter implements ClienteRepositoryPort {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Override
    public Cliente save(Cliente cliente) {
        ClienteEntity clienteEntity = clienteMapper.toClienteEntity(cliente);
        ClienteEntity clienteSalvo = clienteRepository.save(clienteEntity);
        return clienteMapper.toClienteDomain(clienteSalvo);
    }

    @Override
    public Optional<Cliente> findById(UUID id) {
        return clienteRepository.findById(id).map(clienteMapper::toClienteDomain);
    }

    @Override
    public List<Cliente> findAll() {
        return clienteRepository.findAll().stream().map(clienteMapper::toClienteDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        clienteRepository.deleteById(id);
    }

    @Override
    public Optional<Cliente> findByEmail(String email) {
        return Optional.empty();
    }
}
