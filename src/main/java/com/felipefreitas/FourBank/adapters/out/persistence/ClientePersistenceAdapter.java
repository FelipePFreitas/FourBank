package com.felipefreitas.FourBank.adapters.out.persistence;

import com.felipefreitas.FourBank.adapters.out.persistence.entity.ClienteEntity;
import com.felipefreitas.FourBank.adapters.out.persistence.mapper.ClienteMapper;
import com.felipefreitas.FourBank.adapters.out.persistence.repository.ClienteRepository;
import com.felipefreitas.FourBank.domain.model.Cliente;
import com.felipefreitas.FourBank.ports.out.ClientePort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ClientePersistenceAdapter implements ClientePort {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @Override
    public Cliente save(Cliente cliente) {
        ClienteEntity clienteEntity = clienteMapper.toEntity(cliente);
        ClienteEntity clienteSalvo = clienteRepository.save(clienteEntity);
        return clienteMapper.toDomain(clienteSalvo);
    }

    @Override
    public Optional<Cliente> findById(UUID id) {
        return clienteRepository.findById(id).map(clienteMapper::toDomain);
    }

    @Override
    public List<Cliente> findAll() {
        return clienteRepository.findAll().stream().map(clienteMapper::toDomain).toList();
    }

    @Override
    public void deleteById(UUID id) {
        clienteRepository.deleteById(id);
    }

    @Override
        public Optional<Cliente> findByEmail(String email) {
            return clienteRepository.findByEmail(email)
                    .map(clienteMapper::toDomain);
        }


    @Override
        public Optional<Cliente> findByDocumento(String cpf) {
            return clienteRepository.findByDocumento(cpf)
                    .map(clienteMapper::toDomain);
        }
}
