package com.felipefreitas.FourBank.service;

import com.felipefreitas.FourBank.dto.transacao.TransacaoResponseDTO;
import com.felipefreitas.FourBank.entity.ContaEntity;
import com.felipefreitas.FourBank.entity.TransacaoEntity;
import com.felipefreitas.FourBank.enums.ErrorEnum;
import com.felipefreitas.FourBank.enums.StatusTransacao;
import com.felipefreitas.FourBank.enums.TipoTransacao;
import com.felipefreitas.FourBank.exceptions.BaseExceptions;
import com.felipefreitas.FourBank.repository.ContaRepository;
import com.felipefreitas.FourBank.repository.TransacaoRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final ContaRepository contaRepository;


    @Transactional
    public TransacaoResponseDTO pix(String loginUsuarioAutenticado, String chavePix, BigDecimal valor) {
        log.info("Iniciando transferência Pix para login={}", loginUsuarioAutenticado);

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Falha na transferência Pix: valor inválido para login={}", loginUsuarioAutenticado);
            throw new BaseExceptions(ErrorEnum.SALDO_NEGATIVO_NULO);
        }

        ContaEntity contaOrigem = contaRepository.findByCliente_Usuario_Login(loginUsuarioAutenticado)
                .orElseThrow(() -> new BaseExceptions(ErrorEnum.NUMERO_CONTA_NAO_EXISTE));

        ContaEntity contaDestino = contaRepository.findByChavesPixContaining(chavePix)
                .orElseThrow(() -> new BaseExceptions(ErrorEnum.CHAVEPIX_INEXISTENTE));


        if (contaOrigem.getSaldo().compareTo(valor) < 0) {
            log.warn("Falha na transferência Pix: saldo insuficiente para contaOrigemId={}", contaOrigem.getId());
            throw new BaseExceptions(ErrorEnum.SALDO_INSUFICIENTE);
        }

        if (contaDestino.getId().equals(contaOrigem.getId())) {
            log.warn("Falha na transferência Pix: conta de origem e destino são iguais. contaId={}", contaOrigem.getId());
            throw new BaseExceptions(ErrorEnum.TRANSACAO_MESMA_CONTA);
        }

        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(valor));

        contaRepository.save(contaOrigem);


        TransacaoEntity transacao = TransacaoEntity
                .builder()
                .contaOrigem(contaOrigem)
                .contaDestino(contaDestino)
                .descricao("Transferência via PIX de " + valor + " da conta " + contaOrigem.getNumeroConta() + " para a conta " + contaDestino.getNumeroConta())
                .valor(valor)
                .tipoTransacao(TipoTransacao.PIX)
                .statusTransacao(StatusTransacao.PENDENTE)
                .criadoEm(LocalDateTime.now())
                .build();

        transacaoRepository.save(transacao);

        try {
            contaDestino.setSaldo(contaDestino.getSaldo().add(valor));
            contaRepository.save(contaDestino);


            transacao.setStatusTransacao(StatusTransacao.CONCLUIDA);
            transacaoRepository.save(transacao);
            log.info("Transferência Pix concluída com sucesso. transacaoId={} contaOrigemId={} contaDestinoId={}",
                    transacao.getId(), contaOrigem.getId(), contaDestino.getId());

        } catch (Exception e) {
            log.error("Erro ao processar transferência PIX para login={}. Estornando valor...", loginUsuarioAutenticado, e);


            contaOrigem.setSaldo(contaOrigem.getSaldo().add(valor));
            transacao.setStatusTransacao(StatusTransacao.CANCELADA);

            transacaoRepository.saveAndFlush(transacao);
            contaRepository.saveAndFlush(contaOrigem);

            throw new BaseExceptions(ErrorEnum.TRANSACAO_PIX_FALHA);
        }

        return new TransacaoResponseDTO(
                transacao.getId(),
                transacao.getTipoTransacao(),
                transacao.getValor(),
                transacao.getDescricao(),
                transacao.getCriadoEm(),
                transacao.getContaOrigem().getId(),
                transacao.getContaDestino().getId());
    }

    @Transactional
    public TransacaoResponseDTO depositar(String loginUsuarioAutenticado, BigDecimal valor) {
        log.info("Iniciando depósito para login={} valor={}", loginUsuarioAutenticado, valor);

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Falha no depósito: valor inválido para login={}", loginUsuarioAutenticado);
            throw new BaseExceptions(ErrorEnum.SALDO_NEGATIVO_NULO);
        }

        ContaEntity contaOrigem = contaRepository.findByCliente_Usuario_Login(loginUsuarioAutenticado)
                .orElseThrow(() -> {
                    log.warn("Falha no depósito: conta não encontrada para login={}", loginUsuarioAutenticado);
                    return new BaseExceptions(ErrorEnum.NUMERO_CONTA_NAO_EXISTE);
                });
        log.info("Conta localizada para depósito. contaId={} saldoAnterior={}",
                contaOrigem.getId(), contaOrigem.getSaldo());

        contaOrigem.setSaldo(contaOrigem.getSaldo().add(valor));

        contaRepository.save(contaOrigem);
        log.info("Saldo atualizado com sucesso. contaId={} saldoAtual={}",
                contaOrigem.getId(), contaOrigem.getSaldo());

        TransacaoEntity transacao = TransacaoEntity.builder()
                .tipoTransacao(TipoTransacao.DEPOSITO)
                .valor(valor)
                .descricao("Depósito de " + valor + " na conta " + contaOrigem.getNumeroConta())
                .criadoEm(LocalDateTime.now())
                .contaOrigem(contaOrigem)
                .contaDestino(null)
                .statusTransacao(StatusTransacao.CONCLUIDA)
                .build();

        transacaoRepository.save(transacao);
        log.info("Depósito concluído com sucesso. transacaoId={} contaId={} valor={}",
                transacao.getId(), contaOrigem.getId(), valor);

        return new TransacaoResponseDTO(
                transacao.getId(),
                transacao.getTipoTransacao(),
                transacao.getValor(),
                transacao.getDescricao(),
                transacao.getCriadoEm(),
                transacao.getContaOrigem().getId(),
                transacao.getContaDestino() != null ? transacao.getContaDestino().getId() : null);
    }


    @Transactional
        public TransacaoResponseDTO saque(String loginUsuarioAutenticado, BigDecimal valor) {
            log.info("Iniciando saque para login={} valor={}", loginUsuarioAutenticado, valor);

            if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("Falha no saque: valor inválido para login={}", loginUsuarioAutenticado);
                throw new BaseExceptions(ErrorEnum.SALDO_NEGATIVO_NULO);
            }

            ContaEntity contaOrigem = contaRepository.findByCliente_Usuario_Login(loginUsuarioAutenticado)
                    .orElseThrow(() -> {
                        log.warn("Falha no saque: conta não encontrada para login={}", loginUsuarioAutenticado);
                        return new BaseExceptions(ErrorEnum.NUMERO_CONTA_NAO_EXISTE);
                    });
            log.info("Conta localizada para saque. contaId={} saldoAnterior={}",
                    contaOrigem.getId(), contaOrigem.getSaldo());

            if (contaOrigem.getSaldo().compareTo(valor) < 0) {
                log.warn("Falha no saque: saldo insuficiente para contaId={}", contaOrigem.getId());
                throw new BaseExceptions(ErrorEnum.SALDO_INSUFICIENTE);
            }

            contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(valor));

            contaRepository.save(contaOrigem);
            log.info("Saldo atualizado com sucesso. contaId={} saldoAtual={}",
                    contaOrigem.getId(), contaOrigem.getSaldo());

            TransacaoEntity transacao = TransacaoEntity.builder()
                    .tipoTransacao(TipoTransacao.SAQUE)
                    .valor(valor)
                    .descricao("Saque de " + valor + " na conta " + contaOrigem.getNumeroConta())
                    .criadoEm(LocalDateTime.now())
                    .contaOrigem(contaOrigem)
                    .contaDestino(null)
                    .statusTransacao(StatusTransacao.CONCLUIDA)
                    .build();

            transacaoRepository.save(transacao);
            log.info("Saque concluído com sucesso. transacaoId={} contaId={} valor={}",
                    transacao.getId(), contaOrigem.getId(), valor);

            return new TransacaoResponseDTO(
                    transacao.getId(),
                    transacao.getTipoTransacao(),
                    transacao.getValor(),
                    transacao.getDescricao(),
                    transacao.getCriadoEm(),
                    transacao.getContaOrigem().getId(),
                    transacao.getContaDestino() != null ? transacao.getContaDestino().getId() : null);
        }

}
