package com.felipefreitas.FourBank.service;


import com.felipefreitas.FourBank.dto.conta.ContaResponseDTO;
import com.felipefreitas.FourBank.entity.ClienteEntity;
import com.felipefreitas.FourBank.entity.ContaEntity;
import com.felipefreitas.FourBank.enums.ClienteTipo;
import com.felipefreitas.FourBank.enums.ErrorEnum;
import com.felipefreitas.FourBank.exceptions.BaseExceptions;
import com.felipefreitas.FourBank.repository.ContaRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@AllArgsConstructor
public class ContaService {

    private final ContaRepository contaRepository;


    public void criarConta(ClienteEntity cliente, BigDecimal saldoInicial) {
        log.info("Iniciando criação de conta para clienteId={}", cliente.getId());

        if (saldoInicial == null || saldoInicial.compareTo(BigDecimal.ZERO) < 0) {
            log.warn("Falha na criação de conta: saldo inicial inválido para clienteId={}", cliente.getId());
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
        log.info("Conta criada com sucesso. contaId={} clienteId={}",
                contaSalva.getId(), cliente.getId());
    }

    public String gerarNumeroConta() {
        // Gera um número aleatório entre 0 e 999999
        int numero = ThreadLocalRandom.current().nextInt(0, 10000000);

        // Converte o int diretamente para String
        return String.format("%07d", numero);
    }

    @Transactional
    @CacheEvict(cacheNames = "contas", key = "#loginUsuarioAutenticado")
    public void cadastrarChavePix(String loginUsuarioAutenticado, String chavePix) {
        log.info("Iniciando cadastro de chave Pix para login={}", loginUsuarioAutenticado);

        if (chavePix == null || chavePix.isBlank()) {
            log.warn("Falha no cadastro de chave Pix: chave nula ou em branco para login={}", loginUsuarioAutenticado);
            throw new BaseExceptions(ErrorEnum.NULO_BRANCO);
        }


        ContaEntity conta = contaRepository.findByCliente_Usuario_Login(loginUsuarioAutenticado)
                .orElseThrow(() -> new BaseExceptions(ErrorEnum.NUMERO_CONTA_NAO_EXISTE));


        if (contaRepository.findByChavesPixContaining(chavePix).isPresent()) {
            log.warn("Falha no cadastro de chave Pix: chave já cadastrada para login={}", loginUsuarioAutenticado);
            throw new BaseExceptions(ErrorEnum.CHAVEPIX_JACADASTRADA);
        }


        int maxChavesPix = conta.getCliente().getClienteTipo().equals(ClienteTipo.PESSOA_JURIDICA) ? 20 : 5;


        if (conta.getChavesPix().size() >= maxChavesPix) {
            log.warn("Falha no cadastro de chave Pix: limite atingido para contaId={}", conta.getId());
            throw new BaseExceptions(ErrorEnum.LIMITE_CHAVEPIX);
        }

        String chaveFormatada = chavePix.trim().toLowerCase();

        conta.getChavesPix().add(chaveFormatada);
        contaRepository.save(conta);
        log.info("Chave Pix cadastrada com sucesso para contaId={}", conta.getId());
    }


    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "contas", key = "#loginUsuarioAutenticado", unless = "#result == null")
    public ContaResponseDTO consultarDadosConta(String loginUsuarioAutenticado) {
        log.info("Consultando dados da conta para login={}", loginUsuarioAutenticado);

        ContaEntity conta = contaRepository.findByCliente_Usuario_Login(loginUsuarioAutenticado)
                .orElseThrow(() -> new BaseExceptions(ErrorEnum.NUMERO_CONTA_NAO_EXISTE));
        log.info("Consulta de dados da conta concluída com sucesso. contaId={}", conta.getId());

        return new ContaResponseDTO(
                conta.getId(),
                conta.getAgencia(),
                conta.getNumeroConta(),
                conta.getSaldo(),
                conta.getCliente().getId()
        );

    }
}