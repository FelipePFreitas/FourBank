package com.felipefreitas.FourBank.service;

import com.felipefreitas.FourBank.dto.transacao.TransacaoResponseDTO;
import com.felipefreitas.FourBank.dto.transacao.TransferenciaRequestDTO;
import com.felipefreitas.FourBank.entity.ClientePFEntity;
import com.felipefreitas.FourBank.entity.ContaEntity;
import com.felipefreitas.FourBank.entity.TransacaoEntity;
import com.felipefreitas.FourBank.entity.UsuarioEntity;
import com.felipefreitas.FourBank.enums.ClienteTipo;
import com.felipefreitas.FourBank.enums.ErrorEnum;
import com.felipefreitas.FourBank.enums.StatusTransacao;
import com.felipefreitas.FourBank.enums.TipoConta;
import com.felipefreitas.FourBank.enums.TipoTransacao;
import com.felipefreitas.FourBank.exceptions.BaseExceptions;
import com.felipefreitas.FourBank.repository.ContaRepository;
import com.felipefreitas.FourBank.repository.TransacaoRepository;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransacaoService - comportamento bancário")
class TransacaoServiceTest {

    private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");
    private static final BigDecimal LIMITE_TRANSFERENCIA = new BigDecimal("5000.00");
    private static final BigDecimal TAXA_TRANSFERENCIA = new BigDecimal("2.00");
    private static final int TRANSFERENCIAS_GRATUITAS = 3;
    private static final String LOGIN_ORIGEM = "origem.login";
    private static final String LOGIN_DESTINO = "destino.login";
    private static final UUID CONTA_ORIGEM_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID CONTA_DESTINO_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID CLIENTE_ORIGEM_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID CLIENTE_DESTINO_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID TRANSACAO_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private ContaRepository contaRepository;

    private TransacaoService serviceHorarioAberto;
    private TransacaoService serviceHorarioRestrito;

    @BeforeEach
    void setUp() {
        serviceHorarioAberto = new TransacaoService(
                transacaoRepository,
                contaRepository,
                LIMITE_TRANSFERENCIA,
                TAXA_TRANSFERENCIA,
                TRANSFERENCIAS_GRATUITAS,
                LocalTime.MIN,
                LocalTime.MAX);

        serviceHorarioRestrito = new TransacaoService(
                transacaoRepository,
                contaRepository,
                LIMITE_TRANSFERENCIA,
                TAXA_TRANSFERENCIA,
                TRANSFERENCIAS_GRATUITAS,
                LocalTime.of(23, 59, 59),
                LocalTime.of(23, 59, 59));
    }

    @Nested
    @DisplayName("Transferências imediatas")
    class TransferenciasImediatas {

        @Test
        @DisplayName("Deve transferir quando os dados forem válidos e houver saldo suficiente")
        void shouldTransferSuccessfullyWhenDataIsValid() {
            // Given
            ContaEntity contaOrigem = conta(LOGIN_ORIGEM, "Conta Origem", "11111111111", ClienteTipo.PESSOA_FISICA,
                    new BigDecimal("500.00"), "1234567", CONTA_ORIGEM_ID, CLIENTE_ORIGEM_ID);
            ContaEntity contaDestino = conta(LOGIN_DESTINO, "Beneficiário Final", "98765432100", ClienteTipo.PESSOA_FISICA,
                    new BigDecimal("250.00"), "7654321", CONTA_DESTINO_ID, CLIENTE_DESTINO_ID);
            TransferenciaRequestDTO request = new TransferenciaRequestDTO(
                    " Beneficiário Final ",
                    " 98765432100 ",
                    " fourbank ",
                    " 0001 ",
                    " 7654321 ",
                    TipoConta.CC,
                    new BigDecimal("100.00"),
                    null);

            when(contaRepository.findWithLockByCliente_Usuario_Login(LOGIN_ORIGEM)).thenReturn(Optional.of(contaOrigem));
            when(contaRepository.findWithLockByAgenciaAndNumeroConta("0001", "7654321")).thenReturn(Optional.of(contaDestino));
            when(transacaoRepository.countByContaOrigemAndTipoTransacaoAndStatusTransacaoAndCriadoEmBetween(
                    eq(contaOrigem), eq(TipoTransacao.TRANSFERENCIA), eq(StatusTransacao.CONCLUIDA), any(), any()))
                    .thenReturn(0L);
            when(transacaoRepository.save(any(TransacaoEntity.class))).thenAnswer(invocation -> {
                TransacaoEntity transacao = invocation.getArgument(0);
                transacao.setId(TRANSACAO_ID);
                return transacao;
            });

            // When
            TransacaoResponseDTO response = serviceHorarioAberto.transferir(LOGIN_ORIGEM, request);

            // Then
            assertThat(response.id()).isEqualTo(TRANSACAO_ID);
            assertThat(response.tipoTransacao()).isEqualTo(TipoTransacao.TRANSFERENCIA);
            assertThat(response.valor()).isEqualByComparingTo("100.00");
            assertThat(response.descricao()).isEqualTo(
                    "Transferência de 100.00 da conta 1234567 para a conta 7654321 (taxa: 0)");
            assertThat(response.contaOrigemId()).isEqualTo(CONTA_ORIGEM_ID);
            assertThat(response.contaDestinoId()).isEqualTo(CONTA_DESTINO_ID);
            assertThat(contaOrigem.getSaldo()).isEqualByComparingTo("400.00");
            assertThat(contaDestino.getSaldo()).isEqualByComparingTo("350.00");
            verify(transacaoRepository).save(any(TransacaoEntity.class));
            verify(contaRepository).save(contaOrigem);
            verify(contaRepository).save(contaDestino);
        }

        @Test
        @DisplayName("Deve rejeitar valores nulos ou zero")
        void shouldRejectNullOrZeroValue() {
            // Given
            TransferenciaRequestDTO requestBase = new TransferenciaRequestDTO(
                    "Beneficiário Final",
                    "98765432100",
                    "FOURBANK",
                    "0001",
                    "7654321",
                    TipoConta.CC,
                    BigDecimal.ONE,
                    null);

            // When / Then
            for (BigDecimal valor : Arrays.asList(null, BigDecimal.ZERO)) {
                TransferenciaRequestDTO request = new TransferenciaRequestDTO(
                        requestBase.nome(),
                        requestBase.documento(),
                        requestBase.banco(),
                        requestBase.agencia(),
                        requestBase.conta(),
                        requestBase.tipoConta(),
                        valor,
                        requestBase.agendadaPara());

                assertThatThrownBy(() -> serviceHorarioAberto.transferir(LOGIN_ORIGEM, request))
                        .isInstanceOf(BaseExceptions.class)
                        .hasMessage(ErrorEnum.VALOR_TRANSFERENCIA_INVALIDO.getErrorMessage());
            }

            verifyNoInteractions(contaRepository, transacaoRepository);
        }

        @Test
        @DisplayName("Deve rejeitar banco de destino não suportado")
        void shouldRejectUnsupportedBank() {
            // Given
            TransferenciaRequestDTO request = new TransferenciaRequestDTO(
                    "Beneficiário Final",
                    "98765432100",
                    "OUTRO-BANCO",
                    "0001",
                    "7654321",
                    TipoConta.CC,
                    new BigDecimal("100.00"),
                    null);

            // When / Then
            assertThatThrownBy(() -> serviceHorarioAberto.transferir(LOGIN_ORIGEM, request))
                    .isInstanceOf(BaseExceptions.class)
                    .hasMessage(ErrorEnum.BANCO_NAO_SUPORTADO.getErrorMessage());
            verifyNoInteractions(contaRepository, transacaoRepository);
        }

        @Test
        @DisplayName("Deve rejeitar transferência para a mesma conta")
        void shouldRejectTransferToSameAccount() {
            // Given
            ContaEntity contaCompartilhada = conta(LOGIN_ORIGEM, "Beneficiário Final", "98765432100",
                    ClienteTipo.PESSOA_FISICA, new BigDecimal("500.00"), "1234567", CONTA_ORIGEM_ID, CLIENTE_ORIGEM_ID);
            TransferenciaRequestDTO request = new TransferenciaRequestDTO(
                    "Beneficiário Final",
                    "98765432100",
                    "FOURBANK",
                    "0001",
                    "1234567",
                    TipoConta.CC,
                    new BigDecimal("100.00"),
                    null);

            when(contaRepository.findWithLockByCliente_Usuario_Login(LOGIN_ORIGEM)).thenReturn(Optional.of(contaCompartilhada));
            when(contaRepository.findWithLockByAgenciaAndNumeroConta("0001", "1234567")).thenReturn(Optional.of(contaCompartilhada));

            // When / Then
            assertThatThrownBy(() -> serviceHorarioAberto.transferir(LOGIN_ORIGEM, request))
                    .isInstanceOf(BaseExceptions.class)
                    .hasMessage(ErrorEnum.TRANSACAO_MESMA_CONTA.getErrorMessage());
            verify(contaRepository, never()).save(any());
            verify(transacaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve rejeitar favorecido divergente do destino")
        void shouldRejectInvalidFavoredParty() {
            // Given
            ContaEntity contaOrigem = conta(LOGIN_ORIGEM, "Conta Origem", "11111111111", ClienteTipo.PESSOA_FISICA,
                    new BigDecimal("500.00"), "1234567", CONTA_ORIGEM_ID, CLIENTE_ORIGEM_ID);
            ContaEntity contaDestino = conta(LOGIN_DESTINO, "Outro Nome", "00000000000", ClienteTipo.PESSOA_FISICA,
                    new BigDecimal("250.00"), "7654321", CONTA_DESTINO_ID, CLIENTE_DESTINO_ID);
            TransferenciaRequestDTO request = new TransferenciaRequestDTO(
                    "Nome Invalido",
                    "12312312312",
                    "FOURBANK",
                    "0001",
                    "7654321",
                    TipoConta.CC,
                    new BigDecimal("100.00"),
                    null);

            when(contaRepository.findWithLockByCliente_Usuario_Login(LOGIN_ORIGEM)).thenReturn(Optional.of(contaOrigem));
            when(contaRepository.findWithLockByAgenciaAndNumeroConta("0001", "7654321")).thenReturn(Optional.of(contaDestino));

            // When / Then
            assertThatThrownBy(() -> serviceHorarioAberto.transferir(LOGIN_ORIGEM, request))
                    .isInstanceOf(BaseExceptions.class)
                    .hasMessage(ErrorEnum.DADOS_FAVORECIDO_INVALIDOS.getErrorMessage());
        }

        @Test
        @DisplayName("Deve rejeitar transferência quando a conta de origem não existir")
        void shouldRejectWhenOriginAccountIsMissing() {
            // Given
            TransferenciaRequestDTO request = new TransferenciaRequestDTO(
                    "Beneficiário Final",
                    "98765432100",
                    "FOURBANK",
                    "0001",
                    "7654321",
                    TipoConta.CC,
                    new BigDecimal("100.00"),
                    null);

            when(contaRepository.findWithLockByCliente_Usuario_Login(LOGIN_ORIGEM)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> serviceHorarioAberto.transferir(LOGIN_ORIGEM, request))
                    .isInstanceOf(BaseExceptions.class)
                    .hasMessage(ErrorEnum.NUMERO_CONTA_NAO_EXISTE.getErrorMessage());
        }

        @Test
        @DisplayName("Deve rejeitar transferências fora do horário bancário")
        void shouldRejectWhenOutsideBankingHours() {
            // Given
            ContaEntity contaOrigem = conta(LOGIN_ORIGEM, "Conta Origem", "11111111111", ClienteTipo.PESSOA_FISICA,
                    new BigDecimal("500.00"), "1234567", CONTA_ORIGEM_ID, CLIENTE_ORIGEM_ID);
            ContaEntity contaDestino = conta(LOGIN_DESTINO, "Beneficiário Final", "98765432100", ClienteTipo.PESSOA_FISICA,
                    new BigDecimal("250.00"), "7654321", CONTA_DESTINO_ID, CLIENTE_DESTINO_ID);
            TransferenciaRequestDTO request = new TransferenciaRequestDTO(
                    "Beneficiário Final",
                    "98765432100",
                    "FOURBANK",
                    "0001",
                    "7654321",
                    TipoConta.CC,
                    new BigDecimal("100.00"),
                    null);

            when(contaRepository.findWithLockByCliente_Usuario_Login(LOGIN_ORIGEM)).thenReturn(Optional.of(contaOrigem));
            when(contaRepository.findWithLockByAgenciaAndNumeroConta("0001", "7654321")).thenReturn(Optional.of(contaDestino));

            // When / Then
            assertThatThrownBy(() -> serviceHorarioRestrito.transferir(LOGIN_ORIGEM, request))
                    .isInstanceOf(BaseExceptions.class)
                    .hasMessage(ErrorEnum.FORA_HORARIO_TRANSFERENCIA.getErrorMessage());
            verify(contaRepository, never()).save(any());
            verify(transacaoRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Transferências agendadas")
    class TransferenciasAgendadas {

        @Test
        @DisplayName("Deve agendar transferência futura em dia útil")
        void shouldScheduleTransferWhenFutureBusinessDay() {
            // Given
            ContaEntity contaOrigem = conta(LOGIN_ORIGEM, "Conta Origem", "11111111111", ClienteTipo.PESSOA_FISICA,
                    new BigDecimal("500.00"), "1234567", CONTA_ORIGEM_ID, CLIENTE_ORIGEM_ID);
            ContaEntity contaDestino = conta(LOGIN_DESTINO, "Beneficiário Final", "98765432100", ClienteTipo.PESSOA_FISICA,
                    new BigDecimal("250.00"), "7654321", CONTA_DESTINO_ID, CLIENTE_DESTINO_ID);
            LocalDateTime agendadaPara = proximoDiaUtilAmanha(LocalTime.of(9, 0));
            TransferenciaRequestDTO request = new TransferenciaRequestDTO(
                    "Beneficiário Final",
                    "98765432100",
                    "FOURBANK",
                    "0001",
                    "7654321",
                    TipoConta.CC,
                    new BigDecimal("100.00"),
                    agendadaPara);

            when(contaRepository.findWithLockByCliente_Usuario_Login(LOGIN_ORIGEM)).thenReturn(Optional.of(contaOrigem));
            when(contaRepository.findWithLockByAgenciaAndNumeroConta("0001", "7654321")).thenReturn(Optional.of(contaDestino));
            when(transacaoRepository.save(any(TransacaoEntity.class))).thenAnswer(invocation -> {
                TransacaoEntity transacao = invocation.getArgument(0);
                transacao.setId(TRANSACAO_ID);
                return transacao;
            });

            // When
            TransacaoResponseDTO response = serviceHorarioAberto.transferir(LOGIN_ORIGEM, request);

            // Then
            assertThat(response.id()).isEqualTo(TRANSACAO_ID);
            assertThat(response.tipoTransacao()).isEqualTo(TipoTransacao.TRANSFERENCIA);
            assertThat(response.valor()).isEqualByComparingTo("100.00");
            assertThat(response.descricao()).isEqualTo("Transferência agendada");
            assertThat(response.contaOrigemId()).isEqualTo(CONTA_ORIGEM_ID);
            assertThat(response.contaDestinoId()).isEqualTo(CONTA_DESTINO_ID);
            verify(contaRepository, never()).save(any());

            ArgumentCaptor<TransacaoEntity> captor = ArgumentCaptor.forClass(TransacaoEntity.class);
            verify(transacaoRepository).save(captor.capture());
            assertThat(captor.getValue().getStatusTransacao()).isEqualTo(StatusTransacao.PENDENTE);
            assertThat(captor.getValue().getAgendadaPara()).isEqualTo(agendadaPara);
            assertThat(captor.getValue().getTipoTransacao()).isEqualTo(TipoTransacao.TRANSFERENCIA);
        }

        @Test
        @DisplayName("Deve rejeitar agendamentos no passado ou em dia não útil")
        void shouldRejectPastOrWeekendSchedule() {
            // Given
            ContaEntity contaOrigem = conta(LOGIN_ORIGEM, "Conta Origem", "11111111111", ClienteTipo.PESSOA_FISICA,
                    new BigDecimal("500.00"), "1234567", CONTA_ORIGEM_ID, CLIENTE_ORIGEM_ID);
            ContaEntity contaDestino = conta(LOGIN_DESTINO, "Beneficiário Final", "98765432100", ClienteTipo.PESSOA_FISICA,
                    new BigDecimal("250.00"), "7654321", CONTA_DESTINO_ID, CLIENTE_DESTINO_ID);
            when(contaRepository.findWithLockByCliente_Usuario_Login(LOGIN_ORIGEM)).thenReturn(Optional.of(contaOrigem));
            when(contaRepository.findWithLockByAgenciaAndNumeroConta("0001", "7654321")).thenReturn(Optional.of(contaDestino));

            TransferenciaRequestDTO requestBase = new TransferenciaRequestDTO(
                    "Beneficiário Final",
                    "98765432100",
                    "FOURBANK",
                    "0001",
                    "7654321",
                    TipoConta.CC,
                    new BigDecimal("100.00"),
                    LocalDateTime.now(ZONE_ID));

            // When / Then
            List<LocalDateTime> agendamentosInvalidos = List.of(
                    LocalDateTime.now(ZONE_ID).minusMinutes(1),
                    proximoSabadoAmanha(LocalTime.of(9, 0)));

            for (LocalDateTime agendadaPara : agendamentosInvalidos) {
                TransferenciaRequestDTO request = new TransferenciaRequestDTO(
                        requestBase.nome(),
                        requestBase.documento(),
                        requestBase.banco(),
                        requestBase.agencia(),
                        requestBase.conta(),
                        requestBase.tipoConta(),
                        requestBase.valor(),
                        agendadaPara);

                assertThatThrownBy(() -> serviceHorarioAberto.transferir(LOGIN_ORIGEM, request))
                        .isInstanceOf(BaseExceptions.class)
                        .hasMessage(ErrorEnum.AGENDAMENTO_INVALIDO.getErrorMessage());
            }
        }
    }

    @Nested
    @DisplayName("Processamento de agendamentos")
    class ProcessamentoAgendamentos {

        @Test
        @DisplayName("Deve executar agendamentos pendentes quando houver saldo suficiente")
        void shouldExecuteScheduledTransferWhenBalanceIsEnough() {
            // Given
            Assumptions.assumeTrue(diaUtilAtual(), "O cenário depende de um dia útil atual");

            ContaEntity contaOrigem = conta(LOGIN_ORIGEM, "Conta Origem", "11111111111", ClienteTipo.PESSOA_FISICA,
                    new BigDecimal("102.00"), "1234567", CONTA_ORIGEM_ID, CLIENTE_ORIGEM_ID);
            ContaEntity contaDestino = conta(LOGIN_DESTINO, "Beneficiário Final", "98765432100", ClienteTipo.PESSOA_FISICA,
                    new BigDecimal("300.00"), "7654321", CONTA_DESTINO_ID, CLIENTE_DESTINO_ID);
            TransacaoEntity agendamento = transacaoAgendada(contaOrigem, contaDestino, new BigDecimal("100.00"),
                    LocalDateTime.now(ZONE_ID).minusMinutes(1), StatusTransacao.PENDENTE);

            when(transacaoRepository.findByStatusTransacaoAndAgendadaParaLessThanEqual(eq(StatusTransacao.PENDENTE), any(LocalDateTime.class)))
                    .thenReturn(List.of(agendamento));
            when(contaRepository.findWithLockByCliente_Usuario_Login(LOGIN_ORIGEM)).thenReturn(Optional.of(contaOrigem));
            when(contaRepository.findWithLockByAgenciaAndNumeroConta("0001", "7654321")).thenReturn(Optional.of(contaDestino));
            when(transacaoRepository.countByContaOrigemAndTipoTransacaoAndStatusTransacaoAndCriadoEmBetween(
                    eq(contaOrigem), eq(TipoTransacao.TRANSFERENCIA), eq(StatusTransacao.CONCLUIDA), any(), any()))
                    .thenReturn(3L);
            when(transacaoRepository.save(any(TransacaoEntity.class))).thenAnswer(invocation -> {
                TransacaoEntity transacao = invocation.getArgument(0);
                transacao.setId(TRANSACAO_ID);
                return transacao;
            });

            // When
            serviceHorarioAberto.processarTransferenciasAgendadas();

            // Then
            assertThat(agendamento.getStatusTransacao()).isEqualTo(StatusTransacao.CONCLUIDA);
            assertThat(agendamento.getDescricao()).contains("Transferência de 100.00")
                    .contains("(taxa: 2.00)");
            assertThat(contaOrigem.getSaldo()).isEqualByComparingTo("0.00");
            assertThat(contaDestino.getSaldo()).isEqualByComparingTo("400.00");
            verify(contaRepository).save(contaOrigem);
            verify(contaRepository).save(contaDestino);
            verify(transacaoRepository).save(agendamento);
        }

        @Test
        @DisplayName("Deve cancelar agendamentos quando o saldo for insuficiente")
        void shouldCancelScheduledTransferWhenBalanceIsInsufficient() {
            // Given
            Assumptions.assumeTrue(diaUtilAtual(), "O cenário depende de um dia útil atual");

            ContaEntity contaOrigem = conta(LOGIN_ORIGEM, "Conta Origem", "11111111111", ClienteTipo.PESSOA_FISICA,
                    new BigDecimal("101.00"), "1234567", CONTA_ORIGEM_ID, CLIENTE_ORIGEM_ID);
            ContaEntity contaDestino = conta(LOGIN_DESTINO, "Beneficiário Final", "98765432100", ClienteTipo.PESSOA_FISICA,
                    new BigDecimal("300.00"), "7654321", CONTA_DESTINO_ID, CLIENTE_DESTINO_ID);
            TransacaoEntity agendamento = transacaoAgendada(contaOrigem, contaDestino, new BigDecimal("100.00"),
                    LocalDateTime.now(ZONE_ID).minusMinutes(1), StatusTransacao.PENDENTE);

            when(transacaoRepository.findByStatusTransacaoAndAgendadaParaLessThanEqual(eq(StatusTransacao.PENDENTE), any(LocalDateTime.class)))
                    .thenReturn(List.of(agendamento));
            when(contaRepository.findWithLockByCliente_Usuario_Login(LOGIN_ORIGEM)).thenReturn(Optional.of(contaOrigem));
            when(contaRepository.findWithLockByAgenciaAndNumeroConta("0001", "7654321")).thenReturn(Optional.of(contaDestino));
            when(transacaoRepository.countByContaOrigemAndTipoTransacaoAndStatusTransacaoAndCriadoEmBetween(
                    eq(contaOrigem), eq(TipoTransacao.TRANSFERENCIA), eq(StatusTransacao.CONCLUIDA), any(), any()))
                    .thenReturn(3L);

            // When
            serviceHorarioAberto.processarTransferenciasAgendadas();

            // Then
            assertThat(agendamento.getStatusTransacao()).isEqualTo(StatusTransacao.CANCELADA);
            assertThat(contaOrigem.getSaldo()).isEqualByComparingTo("101.00");
            assertThat(contaDestino.getSaldo()).isEqualByComparingTo("300.00");
            verify(contaRepository, never()).save(any());
            verify(transacaoRepository).save(agendamento);
        }

        @Test
        @DisplayName("Deve ignorar processamento fora do horário permitido")
        void shouldIgnoreProcessingOutsideAllowedHours() {
            // When
            serviceHorarioRestrito.processarTransferenciasAgendadas();

            // Then
            verifyNoInteractions(contaRepository, transacaoRepository);
        }
    }

    @Nested
    @DisplayName("Depósitos e saques")
    class DepositosESaques {

        @Test
        @DisplayName("Deve efetuar depósito quando o valor for válido")
        void shouldDepositSuccessfully() {
            // Given
            ContaEntity conta = conta(LOGIN_ORIGEM, "Conta Origem", "11111111111", ClienteTipo.PESSOA_FISICA,
                    new BigDecimal("100.00"), "1234567", CONTA_ORIGEM_ID, CLIENTE_ORIGEM_ID);
            when(contaRepository.findByCliente_Usuario_Login(LOGIN_ORIGEM)).thenReturn(Optional.of(conta));
            when(transacaoRepository.save(any(TransacaoEntity.class))).thenAnswer(invocation -> {
                TransacaoEntity transacao = invocation.getArgument(0);
                transacao.setId(TRANSACAO_ID);
                return transacao;
            });

            // When
            TransacaoResponseDTO response = serviceHorarioAberto.depositar(LOGIN_ORIGEM, new BigDecimal("25.50"));

            // Then
            assertThat(response.id()).isEqualTo(TRANSACAO_ID);
            assertThat(response.tipoTransacao()).isEqualTo(TipoTransacao.DEPOSITO);
            assertThat(response.valor()).isEqualByComparingTo("25.50");
            assertThat(response.descricao()).isEqualTo("Depósito de 25.50 na conta 1234567");
            assertThat(response.contaOrigemId()).isEqualTo(CONTA_ORIGEM_ID);
            assertThat(response.contaDestinoId()).isNull();
            assertThat(conta.getSaldo()).isEqualByComparingTo("125.50");
            verify(contaRepository).save(conta);
            verify(transacaoRepository).save(any(TransacaoEntity.class));
        }

        @Test
        @DisplayName("Deve rejeitar depósitos com valor nulo ou zero")
        void shouldRejectNullOrZeroDepositValue() {
            // When / Then
            for (BigDecimal valor : Arrays.asList(null, BigDecimal.ZERO)) {
                assertThatThrownBy(() -> serviceHorarioAberto.depositar(LOGIN_ORIGEM, valor))
                        .isInstanceOf(BaseExceptions.class)
                        .hasMessage(ErrorEnum.SALDO_NEGATIVO_NULO.getErrorMessage());
            }

            verifyNoInteractions(contaRepository, transacaoRepository);
        }

        @Test
        @DisplayName("Deve rejeitar depósito quando a conta não existir")
        void shouldRejectDepositWhenAccountIsMissing() {
            // Given
            when(contaRepository.findByCliente_Usuario_Login(LOGIN_ORIGEM)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> serviceHorarioAberto.depositar(LOGIN_ORIGEM, new BigDecimal("10.00")))
                    .isInstanceOf(BaseExceptions.class)
                    .hasMessage(ErrorEnum.NUMERO_CONTA_NAO_EXISTE.getErrorMessage());
            verify(contaRepository).findByCliente_Usuario_Login(LOGIN_ORIGEM);
            verify(transacaoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve efetuar saque quando o valor for válido")
        void shouldWithdrawSuccessfully() {
            // Given
            ContaEntity conta = conta(LOGIN_ORIGEM, "Conta Origem", "11111111111", ClienteTipo.PESSOA_FISICA,
                    new BigDecimal("100.00"), "1234567", CONTA_ORIGEM_ID, CLIENTE_ORIGEM_ID);
            when(contaRepository.findByCliente_Usuario_Login(LOGIN_ORIGEM)).thenReturn(Optional.of(conta));
            when(transacaoRepository.save(any(TransacaoEntity.class))).thenAnswer(invocation -> {
                TransacaoEntity transacao = invocation.getArgument(0);
                transacao.setId(TRANSACAO_ID);
                return transacao;
            });

            // When
            TransacaoResponseDTO response = serviceHorarioAberto.saque(LOGIN_ORIGEM, new BigDecimal("40.00"));

            // Then
            assertThat(response.id()).isEqualTo(TRANSACAO_ID);
            assertThat(response.tipoTransacao()).isEqualTo(TipoTransacao.SAQUE);
            assertThat(response.valor()).isEqualByComparingTo("40.00");
            assertThat(response.descricao()).isEqualTo("Saque de 40.00 na conta 1234567");
            assertThat(response.contaOrigemId()).isEqualTo(CONTA_ORIGEM_ID);
            assertThat(response.contaDestinoId()).isNull();
            assertThat(conta.getSaldo()).isEqualByComparingTo("60.00");
            verify(contaRepository).save(conta);
            verify(transacaoRepository).save(any(TransacaoEntity.class));
        }

        @Test
        @DisplayName("Deve rejeitar saques com valor nulo ou zero")
        void shouldRejectNullOrZeroWithdrawValue() {
            // When / Then
            for (BigDecimal valor : Arrays.asList(null, BigDecimal.ZERO)) {
                assertThatThrownBy(() -> serviceHorarioAberto.saque(LOGIN_ORIGEM, valor))
                        .isInstanceOf(BaseExceptions.class)
                        .hasMessage(ErrorEnum.SALDO_NEGATIVO_NULO.getErrorMessage());
            }

            verifyNoInteractions(contaRepository, transacaoRepository);
        }

        @Test
        @DisplayName("Deve rejeitar saque quando o saldo for insuficiente")
        void shouldRejectWithdrawWhenBalanceIsInsufficient() {
            // Given
            ContaEntity conta = conta(LOGIN_ORIGEM, "Conta Origem", "11111111111", ClienteTipo.PESSOA_FISICA,
                    new BigDecimal("20.00"), "1234567", CONTA_ORIGEM_ID, CLIENTE_ORIGEM_ID);
            when(contaRepository.findByCliente_Usuario_Login(LOGIN_ORIGEM)).thenReturn(Optional.of(conta));

            // When / Then
            assertThatThrownBy(() -> serviceHorarioAberto.saque(LOGIN_ORIGEM, new BigDecimal("40.00")))
                    .isInstanceOf(BaseExceptions.class)
                    .hasMessage(ErrorEnum.SALDO_INSUFICIENTE.getErrorMessage());
            verify(contaRepository, never()).save(any());
            verify(transacaoRepository, never()).save(any());
        }
    }

    private ContaEntity conta(String login, String nome, String documento, ClienteTipo clienteTipo,
                              BigDecimal saldo, String numeroConta, UUID contaId, UUID clienteId) {
        UsuarioEntity usuario = UsuarioEntity.builder()
                .login(login)
                .senha("senha123")
                .build();

        ClientePFEntity cliente = new ClientePFEntity();
        cliente.setId(clienteId);
        cliente.setNomeRazaoSocial(nome);
        cliente.setDocumento(documento);
        cliente.setEmail(login + "@example.com");
        cliente.setTelefone("11999999999");
        cliente.setClienteTipo(clienteTipo);
        cliente.setUsuario(usuario);
        usuario.setCliente(cliente);

        return ContaEntity.builder()
                .id(contaId)
                .agencia("0001")
                .numeroConta(numeroConta)
                .saldo(saldo)
                .cliente(cliente)
                .build();
    }

    private TransacaoEntity transacaoAgendada(ContaEntity origem, ContaEntity destino, BigDecimal valor,
                                              LocalDateTime agendadaPara, StatusTransacao status) {
        return TransacaoEntity.builder()
                .id(TRANSACAO_ID)
                .contaOrigem(origem)
                .contaDestino(destino)
                .valor(valor)
                .tipoTransacao(TipoTransacao.TRANSFERENCIA)
                .statusTransacao(status)
                .criadoEm(LocalDateTime.now(ZONE_ID))
                .agendadaPara(agendadaPara)
                .descricao("Transferência agendada")
                .build();
    }

    private LocalDateTime proximoDiaUtilAmanha(LocalTime horario) {
        LocalDate data = LocalDate.now(ZONE_ID).plusDays(1);
        while (data.getDayOfWeek() == DayOfWeek.SATURDAY || data.getDayOfWeek() == DayOfWeek.SUNDAY) {
            data = data.plusDays(1);
        }
        return data.atTime(horario);
    }

    private LocalDateTime proximoSabadoAmanha(LocalTime horario) {
        LocalDate data = LocalDate.now(ZONE_ID).plusDays(1);
        while (data.getDayOfWeek() != DayOfWeek.SATURDAY) {
            data = data.plusDays(1);
        }
        return data.atTime(horario);
    }

    private boolean diaUtilAtual() {
        DayOfWeek dia = LocalDate.now(ZONE_ID).getDayOfWeek();
        return dia != DayOfWeek.SATURDAY && dia != DayOfWeek.SUNDAY;
    }
}
