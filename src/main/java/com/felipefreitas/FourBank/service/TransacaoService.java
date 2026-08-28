package com.felipefreitas.FourBank.service;

import com.felipefreitas.FourBank.dto.transacao.TransacaoResponseDTO;
import com.felipefreitas.FourBank.dto.transacao.TransferenciaRequestDTO;
import com.felipefreitas.FourBank.entity.ContaEntity;
import com.felipefreitas.FourBank.entity.TransacaoEntity;
import com.felipefreitas.FourBank.enums.ErrorEnum;
import com.felipefreitas.FourBank.enums.StatusTransacao;
import com.felipefreitas.FourBank.enums.TipoTransacao;
import com.felipefreitas.FourBank.exceptions.BaseExceptions;
import com.felipefreitas.FourBank.messaging.TransferenciaConcluidaEvent;
import com.felipefreitas.FourBank.repository.ContaRepository;
import com.felipefreitas.FourBank.repository.TransacaoRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final ContaRepository contaRepository;
    private final ApplicationEventPublisher eventPublisher;
    private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");
    private static final String BANCO_FOURBANK = "FOURBANK";
    private final BigDecimal limiteTransferencia;
    private final BigDecimal taxaTransferencia;
    private final int transferenciasGratuitas;
    private final LocalTime horarioInicial;
    private final LocalTime horarioFinal;

    @Autowired
    public TransacaoService(
            TransacaoRepository transacaoRepository,
            ContaRepository contaRepository,
            ApplicationEventPublisher eventPublisher,
            @Value("${transferencia.limite:5000.00}") BigDecimal limiteTransferencia,
            @Value("${transferencia.taxa:2.00}") BigDecimal taxaTransferencia,
            @Value("${transferencia.gratuitas:3}") int transferenciasGratuitas,
            @Value("${transferencia.horario-inicial:08:00}") LocalTime horarioInicial,
            @Value("${transferencia.horario-final:17:00}") LocalTime horarioFinal) {
        this.transacaoRepository = transacaoRepository;
        this.contaRepository = contaRepository;
        this.eventPublisher = eventPublisher;
        this.limiteTransferencia = limiteTransferencia;
        this.taxaTransferencia = taxaTransferencia;
        this.transferenciasGratuitas = transferenciasGratuitas;
        this.horarioInicial = horarioInicial;
        this.horarioFinal = horarioFinal;
    }

    public TransacaoService(
            TransacaoRepository transacaoRepository,
            ContaRepository contaRepository,
            BigDecimal limiteTransferencia,
            BigDecimal taxaTransferencia,
            int transferenciasGratuitas,
            LocalTime horarioInicial,
            LocalTime horarioFinal) {
        this(transacaoRepository, contaRepository, null, limiteTransferencia, taxaTransferencia,
                transferenciasGratuitas, horarioInicial, horarioFinal);
    }

    @Transactional
    @CacheEvict(cacheNames = "contas", allEntries = true)
    public TransacaoResponseDTO transferir(String loginUsuarioAutenticado, TransferenciaRequestDTO request) {
        log.info("Iniciando transferência bancária");
        validarTransferencia(request);

        ContaEntity contaOrigem = contaRepository.findWithLockByCliente_Usuario_Login(loginUsuarioAutenticado)
                .orElseThrow(() -> new BaseExceptions(ErrorEnum.NUMERO_CONTA_NAO_EXISTE));
        ContaEntity contaDestino = contaRepository.findWithLockByAgenciaAndNumeroConta(
                        request.agencia().trim(), request.conta().trim())
                .orElseThrow(() -> new BaseExceptions(ErrorEnum.CONTA_DESTINO_NAO_EXISTE));

        validarFavorecido(request, contaOrigem, contaDestino);
        LocalDateTime agora = LocalDateTime.now(ZONE_ID);

        if (request.agendadaPara() != null) {
            validarAgendamento(request.agendadaPara(), agora);
            TransacaoEntity agendamento = criarTransferencia(
                    contaOrigem, contaDestino, request.valor(), request.agendadaPara(), StatusTransacao.PENDENTE, null);
            transacaoRepository.save(agendamento);
            log.info("Transferência agendada. transacaoId={} origemId={} destinoId={} agendadaPara={}",
                    agendamento.getId(), contaOrigem.getId(), contaDestino.getId(), request.agendadaPara());
            return gerarResposta(agendamento);
        }

        validarHorarioBancario(agora);
        TransacaoResponseDTO resposta = executarTransferencia(contaOrigem, contaDestino, request.valor(), agora, null);
        log.info("Transferência concluída. transacaoId={} origemId={} destinoId={}",
                resposta.id(), resposta.contaOrigemId(), resposta.contaDestinoId());
        return resposta;
    }

    @Scheduled(fixedDelayString = "${transferencia.agendamento.intervalo-ms:5000}")
    @Transactional
    @CacheEvict(cacheNames = "contas", allEntries = true)
    public void processarTransferenciasAgendadas() {
        LocalDateTime agora = LocalDateTime.now(ZONE_ID);
        if (!diaUtil(agora) || agora.toLocalTime().isBefore(horarioInicial)
                || agora.toLocalTime().isAfter(horarioFinal)) {
            return;
        }

        List<TransacaoEntity> agendamentos = transacaoRepository
                .findByStatusTransacaoAndAgendadaParaLessThanEqual(StatusTransacao.PENDENTE, agora);
        for (TransacaoEntity agendamento : agendamentos) {
            executarTransferenciaAgendada(agendamento, agora);
        }
    }

    private void executarTransferenciaAgendada(TransacaoEntity agendamento, LocalDateTime agora) {
        ContaEntity contaOrigem = contaRepository.findWithLockByCliente_Usuario_Login(
                        agendamento.getContaOrigem().getCliente().getUsuario().getLogin())
                .orElseThrow(() -> new BaseExceptions(ErrorEnum.NUMERO_CONTA_NAO_EXISTE));
        ContaEntity contaDestino = contaRepository.findWithLockByAgenciaAndNumeroConta(
                        agendamento.getContaDestino().getAgencia(), agendamento.getContaDestino().getNumeroConta())
                .orElseThrow(() -> new BaseExceptions(ErrorEnum.CONTA_DESTINO_NAO_EXISTE));

        BigDecimal taxa = calcularTaxa(contaOrigem, agora);
        if (!possuiSaldo(contaOrigem, agendamento.getValor().add(taxa))) {
            agendamento.setStatusTransacao(StatusTransacao.CANCELADA);
            transacaoRepository.save(agendamento);
            log.warn("Transferência agendada cancelada por saldo insuficiente. transacaoId={} origemId={} destinoId={}",
                    agendamento.getId(), contaOrigem.getId(), contaDestino.getId());
            return;
        }

        executarTransferencia(contaOrigem, contaDestino, agendamento.getValor(), agora, agendamento);
        log.info("Transferência agendada concluída. transacaoId={} origemId={} destinoId={}",
                agendamento.getId(), contaOrigem.getId(), contaDestino.getId());
    }

    private TransacaoResponseDTO executarTransferencia(
            ContaEntity contaOrigem,
            ContaEntity contaDestino,
            BigDecimal valor,
            LocalDateTime agora,
            TransacaoEntity transacaoExistente) {
        BigDecimal taxa = calcularTaxa(contaOrigem, agora);
        BigDecimal totalDebitado = valor.add(taxa);

        if (!possuiSaldo(contaOrigem, totalDebitado)) {
            throw new BaseExceptions(ErrorEnum.SALDO_INSUFICIENTE);
        }

        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(totalDebitado));
        contaDestino.setSaldo(contaDestino.getSaldo().add(valor));
        contaRepository.save(contaOrigem);
        contaRepository.save(contaDestino);

        TransacaoEntity transacao = transacaoExistente == null
                ? criarTransferencia(contaOrigem, contaDestino, valor, null, StatusTransacao.CONCLUIDA, taxa)
                : transacaoExistente;
        transacao.setCriadoEm(agora);
        transacao.setStatusTransacao(StatusTransacao.CONCLUIDA);
        transacao.setDescricao(descricaoTransferencia(valor, taxa, contaOrigem, contaDestino));
        transacaoRepository.save(transacao);
        if (eventPublisher != null) {
            eventPublisher.publishEvent(new TransferenciaConcluidaEvent(
                    UUID.randomUUID(), transacao.getId(), contaOrigem.getId(), contaDestino.getId(),
                    valor, agora));
        }
        return gerarResposta(transacao);
    }

    private TransacaoEntity criarTransferencia(
            ContaEntity origem,
            ContaEntity destino,
            BigDecimal valor,
            LocalDateTime agendadaPara,
            StatusTransacao status,
            BigDecimal taxa) {
        return TransacaoEntity.builder()
                .contaOrigem(origem)
                .contaDestino(destino)
                .valor(valor)
                .tipoTransacao(TipoTransacao.TRANSFERENCIA)
                .statusTransacao(status)
                .criadoEm(LocalDateTime.now(ZONE_ID))
                .agendadaPara(agendadaPara)
                .descricao(taxa == null ? "Transferência agendada" :
                        descricaoTransferencia(valor, taxa, origem, destino))
                .build();
    }

    private BigDecimal calcularTaxa(ContaEntity contaOrigem, LocalDateTime agora) {
        LocalDateTime inicio = agora.toLocalDate().atStartOfDay();
        LocalDateTime fim = inicio.plusDays(1);
        long realizadas = transacaoRepository.countByContaOrigemAndTipoTransacaoAndStatusTransacaoAndCriadoEmBetween(
                contaOrigem, TipoTransacao.TRANSFERENCIA, StatusTransacao.CONCLUIDA, inicio, fim);
        return realizadas < transferenciasGratuitas ? BigDecimal.ZERO : taxaTransferencia;
    }

    private boolean possuiSaldo(ContaEntity conta, BigDecimal valor) {
        return conta.getSaldo().compareTo(valor) >= 0;
    }

    private void validarTransferencia(TransferenciaRequestDTO request) {
        if (request.valor() == null || request.valor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BaseExceptions(ErrorEnum.VALOR_TRANSFERENCIA_INVALIDO);
        }
        if (request.valor().compareTo(limiteTransferencia) > 0) {
            throw new BaseExceptions(ErrorEnum.LIMITE_TRANSFERENCIA_EXCEDIDO);
        }
        if (!BANCO_FOURBANK.equalsIgnoreCase(request.banco().trim())) {
            throw new BaseExceptions(ErrorEnum.BANCO_NAO_SUPORTADO);
        }
    }

    private void validarFavorecido(TransferenciaRequestDTO request, ContaEntity origem, ContaEntity destino) {
        if (origem.getId().equals(destino.getId())) {
            throw new BaseExceptions(ErrorEnum.TRANSACAO_MESMA_CONTA);
        }
        if (!destino.getCliente().getNomeRazaoSocial().equalsIgnoreCase(request.nome().trim())
                || !destino.getCliente().getDocumento().equals(request.documento().trim())) {
            throw new BaseExceptions(ErrorEnum.DADOS_FAVORECIDO_INVALIDOS);
        }
    }

    private void validarHorarioBancario(LocalDateTime agora) {
        if (!diaUtil(agora) || agora.toLocalTime().isBefore(horarioInicial)
                || agora.toLocalTime().isAfter(horarioFinal)) {
            throw new BaseExceptions(ErrorEnum.FORA_HORARIO_TRANSFERENCIA);
        }
    }

    private void validarAgendamento(LocalDateTime agendadaPara, LocalDateTime agora) {
        if (!agendadaPara.isAfter(agora) || !diaUtil(agendadaPara)) {
            throw new BaseExceptions(ErrorEnum.AGENDAMENTO_INVALIDO);
        }
    }

    private boolean diaUtil(LocalDateTime data) {
        DayOfWeek dia = data.getDayOfWeek();
        return dia != DayOfWeek.SATURDAY && dia != DayOfWeek.SUNDAY;
    }

    private String descricaoTransferencia(
            BigDecimal valor, BigDecimal taxa, ContaEntity origem, ContaEntity destino) {
        return "Transferência de " + valor + " da conta " + origem.getNumeroConta()
                + " para a conta " + destino.getNumeroConta() + " (taxa: " + taxa + ")";
    }

    private TransacaoResponseDTO gerarResposta(TransacaoEntity transacao) {
        return new TransacaoResponseDTO(
                transacao.getId(), transacao.getTipoTransacao(), transacao.getValor(),
                transacao.getDescricao(), transacao.getCriadoEm(),
                transacao.getContaOrigem().getId(), transacao.getContaDestino().getId());
    }


    @Transactional
    @CacheEvict(cacheNames = "contas", allEntries = true)
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
    @CacheEvict(cacheNames = "contas", key = "#loginUsuarioAutenticado")
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
    @CacheEvict(cacheNames = "contas", key = "#loginUsuarioAutenticado")
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
