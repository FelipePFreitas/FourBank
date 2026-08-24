package com.felipefreitas.FourBank.service;


import com.felipefreitas.FourBank.entity.ClienteEntity;
import com.felipefreitas.FourBank.entity.ContaEntity;
import com.felipefreitas.FourBank.enums.ErrorEnum;
import com.felipefreitas.FourBank.exceptions.BaseExceptions;
import com.felipefreitas.FourBank.repository.ContaRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@AllArgsConstructor
public class ContaService {

    private final ContaRepository contaRepository;


    public void criarConta(ClienteEntity cliente, BigDecimal saldoInicial) {
        if (saldoInicial == null || saldoInicial.compareTo(BigDecimal.ZERO) < 0) {
            throw new BaseExceptions(ErrorEnum.SALDO_NEGATIVO_NULO);
        }

        String numeroConta;
        boolean contaJaExiste;

        do {
            // 1. Gera um número aleatório (ex: "482934")
            numeroConta = gerarNumeroConta();

            // 2. Vai no banco de dados e checa se já existe alguma conta com esse número
            contaJaExiste = contaRepository.existsByNumeroConta(numeroConta);

            if (contaJaExiste) {
                log.warn("O número de conta {} gerado já existe no banco. Tentando gerar outro...", numeroConta);
            }

        } while (contaJaExiste); // 3. Se existir, o loop repete, gera outro número e testa de novo.

        ContaEntity novaConta = ContaEntity.builder()
                .numeroConta(numeroConta)
                .agencia("0001")
                .saldo(saldoInicial)
                .cliente(cliente)
                .build();

        ContaEntity contaSalva = contaRepository.save(novaConta);
    }

    public String gerarNumeroConta() {
        // Gera um número aleatório entre 0 e 999999
        int numero = ThreadLocalRandom.current().nextInt(0, 10000000);

        // Converte o int diretamente para String
        return String.format("%07d", numero);
    }

    @Transactional(readOnly = true)
    public BigDecimal consultarSaldo(String numeroConta) {
        ContaEntity conta = contaRepository.findByNumeroConta(numeroConta)
                .orElseThrow(() -> new BaseExceptions(ErrorEnum.NUMERO_CONTA_NAO_EXISTE));
        return conta.getSaldo();
    }

}