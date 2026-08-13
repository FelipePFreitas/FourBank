package com.felipefreitas.FourBank.domain.service;

import com.felipefreitas.FourBank.domain.enums.ErrorEnum;
import com.felipefreitas.FourBank.domain.exception.BaseException;
import com.felipefreitas.FourBank.domain.model.Cliente;
import com.felipefreitas.FourBank.domain.util.CpfCnpjValidatorUtils;
import com.felipefreitas.FourBank.domain.util.EmailValidatorUtils;
import com.felipefreitas.FourBank.domain.util.PasswordValidatorUtils;
import com.felipefreitas.FourBank.ports.in.cliente.BuscarClienteIdUseCase;
import com.felipefreitas.FourBank.ports.in.cliente.CadastrarClientePFUseCase;
import com.felipefreitas.FourBank.ports.in.cliente.DeletarClientePorIdUseCase;
import com.felipefreitas.FourBank.ports.in.cliente.ListarTodosOsClienteUseCase;
import com.felipefreitas.FourBank.ports.out.ClienteRepositoryPort;
import com.felipefreitas.FourBank.ports.out.PasswordEncoderPort;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class ClienteService implements BuscarClienteIdUseCase, CadastrarClientePFUseCase, DeletarClientePorIdUseCase, ListarTodosOsClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;

    @Override
    public Cliente buscarClientePorId(UUID id) {

        return clienteRepositoryPort.findById(id).orElseThrow(() -> new BaseException(ErrorEnum.CLIENTE_NAO_ENCONTRADO));
    }

    @Override
    public Cliente cadastrarClientePF(Cliente cliente) {

        if (!EmailValidatorUtils.isValidEmail(cliente.getEmail())) {
            throw new BaseException(ErrorEnum.EMAIL_INVALIDO);
        }

        if (!PasswordValidatorUtils.isValidPassword(cliente.getSenha())) {
            throw new BaseException(ErrorEnum.SENHA_INVALIDA);
        }

        if (!CpfCnpjValidatorUtils.isValidCpf(cliente.getDocumento())) {
            throw new BaseException(ErrorEnum.CPF_INVALIDO);
        }

        if (clienteRepositoryPort.findByEmail(cliente.getEmail()).isPresent()){
            throw new BaseException(ErrorEnum.EMAIL_JA_CADASTRADO);
        }

        if (clienteRepositoryPort.findByDocumento(cliente.getDocumento()).isPresent()){
            throw new BaseException(ErrorEnum.CPF_JA_CADASTRADO);
        }

        String senhaCriptografada = passwordEncoderPort.encode(cliente.getSenha());
        cliente.setSenha(senhaCriptografada);

        return clienteRepositoryPort.save(cliente);
    }

    @Override
    public void deletarClientePorId(UUID id) {
        buscarClientePorId(id);
        clienteRepositoryPort.deleteById(id);
    }

    @Override
    public List<Cliente> listarTodosOsClientes() {
        return clienteRepositoryPort.findAll();
    }
}
