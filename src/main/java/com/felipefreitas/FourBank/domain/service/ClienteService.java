package com.felipefreitas.FourBank.domain.service;

import com.felipefreitas.FourBank.domain.enums.ErrorEnum;
import com.felipefreitas.FourBank.domain.enums.TipoConta;
import com.felipefreitas.FourBank.domain.exception.BaseException;
import com.felipefreitas.FourBank.domain.model.Cliente;
import com.felipefreitas.FourBank.domain.model.Conta;
import com.felipefreitas.FourBank.domain.util.ContaUtil;
import com.felipefreitas.FourBank.domain.util.CpfCnpjValidatorUtils;
import com.felipefreitas.FourBank.domain.util.EmailValidatorUtils;
import com.felipefreitas.FourBank.domain.util.PasswordValidatorUtils;
import com.felipefreitas.FourBank.ports.in.cliente.CadastrarClientePFUseCase;
import com.felipefreitas.FourBank.ports.out.ClienteRepositoryPort;
import com.felipefreitas.FourBank.ports.out.ContaRepositoryPort;
import com.felipefreitas.FourBank.ports.out.PasswordEncoderPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class ClienteService implements CadastrarClientePFUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final ContaRepositoryPort contaRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;

    @Override
    @Transactional
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

        if (clienteRepositoryPort.findByEmail(cliente.getEmail()).isPresent()) {
            throw new BaseException(ErrorEnum.EMAIL_JA_CADASTRADO);
        }

        if (clienteRepositoryPort.findByDocumento(cliente.getDocumento()).isPresent()) {
            throw new BaseException(ErrorEnum.CPF_JA_CADASTRADO);
        }

        String senhaCriptografada = passwordEncoderPort.encode(cliente.getSenha());

        cliente.setSenha(senhaCriptografada);

        Cliente clienteSalvo = clienteRepositoryPort.save(cliente);

        String numeroConta;

        do {
            numeroConta = ContaUtil.gerarNumeroConta();
        } while (contaRepositoryPort.existsByNumeroConta(numeroConta));


        Conta conta = Conta.builder()
                .agencia(ContaUtil.AGENCIA_PADRAO)
                .numeroConta(numeroConta)
                .saldo(BigDecimal.ZERO)
                .cliente(clienteSalvo)
                .tipoConta(TipoConta.PF)
                .build();

        contaRepositoryPort.save(conta);

        return clienteSalvo;
    }
}
