package com.felipefreitas.FourBank.service;

import com.felipefreitas.FourBank.dto.conta.ContaResponseDTO;
import com.felipefreitas.FourBank.entity.ClientePFEntity;
import com.felipefreitas.FourBank.entity.ContaEntity;
import com.felipefreitas.FourBank.entity.UsuarioEntity;
import com.felipefreitas.FourBank.enums.ClienteTipo;
import com.felipefreitas.FourBank.enums.ErrorEnum;
import com.felipefreitas.FourBank.exceptions.BaseExceptions;
import com.felipefreitas.FourBank.repository.ContaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ContaService - comportamento de conta e Pix")
class ContaServiceTest {

    private static final String LOGIN = "cliente.login";
    private static final UUID CLIENTE_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID CONTA_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID CONTA_SALVA_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Mock
    private ContaRepository contaRepository;

    private ContaService contaService;
    private ContaService contaServiceSpy;

    @BeforeEach
    void setUp() {
        contaService = new ContaService(contaRepository);
        contaServiceSpy = spy(contaService);
    }

    @Nested
    @DisplayName("Criação de conta")
    class CriacaoDeConta {

        @Test
        @DisplayName("Deve criar conta com saldo inicial zero e tentar novamente quando o número já existir")
        void shouldCreateAccountRetryingWhenNumberAlreadyExists() {
            // Given
            ClientePFEntity cliente = cliente(ClienteTipo.PESSOA_FISICA);
            doReturn("1234567").doReturn("7654321").when(contaServiceSpy).gerarNumeroConta();
            when(contaRepository.existsByNumeroConta("1234567")).thenReturn(true);
            when(contaRepository.existsByNumeroConta("7654321")).thenReturn(false);
            when(contaRepository.save(any(ContaEntity.class))).thenAnswer(invocation -> {
                ContaEntity conta = invocation.getArgument(0);
                conta.setId(CONTA_SALVA_ID);
                return conta;
            });

            // When
            contaServiceSpy.criarConta(cliente, BigDecimal.ZERO);

            // Then
            ArgumentCaptor<ContaEntity> captor = ArgumentCaptor.forClass(ContaEntity.class);
            verify(contaRepository).save(captor.capture());
            assertThat(captor.getValue().getId()).isEqualTo(CONTA_SALVA_ID);
            assertThat(captor.getValue().getNumeroConta()).isEqualTo("7654321");
            assertThat(captor.getValue().getAgencia()).isEqualTo("0001");
            assertThat(captor.getValue().getSaldo()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(captor.getValue().getCliente()).isSameAs(cliente);
            verify(contaRepository).existsByNumeroConta("1234567");
            verify(contaRepository).existsByNumeroConta("7654321");
        }

        @Test
        @DisplayName("Deve rejeitar saldo inicial nulo ou negativo")
        void shouldRejectNullOrNegativeInitialBalance() {
            // Given
            ClientePFEntity cliente = cliente(ClienteTipo.PESSOA_FISICA);

            // When / Then
            for (BigDecimal saldoInicial : Arrays.asList(null, new BigDecimal("-1.00"))) {
                assertThatThrownBy(() -> contaService.criarConta(cliente, saldoInicial))
                        .isInstanceOf(BaseExceptions.class)
                        .hasMessage(ErrorEnum.SALDO_NEGATIVO_NULO.getErrorMessage());
            }

            verifyNoInteractions(contaRepository);
        }
    }

    @Nested
    @DisplayName("Cadastro de chave Pix")
    class CadastroDeChavePix {

        @Test
        @DisplayName("Deve cadastrar chave Pix normalizada quando houver espaço e letras maiúsculas")
        void shouldRegisterNormalizedPixKey() {
            // Given
            ContaEntity conta = conta(ClienteTipo.PESSOA_FISICA, new BigDecimal("100.00"), "1234567",
                    setOf("outra-chave"));
            when(contaRepository.findByCliente_Usuario_Login(LOGIN)).thenReturn(Optional.of(conta));
            when(contaRepository.findByChavesPixContaining("  NOVA-CHAVE  ")).thenReturn(Optional.empty());

            // When
            contaService.cadastrarChavePix(LOGIN, "  NOVA-CHAVE  ");

            // Then
            ArgumentCaptor<ContaEntity> captor = ArgumentCaptor.forClass(ContaEntity.class);
            verify(contaRepository).save(captor.capture());
            assertThat(captor.getValue().getChavesPix()).contains("nova-chave");
            assertThat(captor.getValue().getChavesPix()).contains("outra-chave");
            assertThat(captor.getValue().getChavesPix()).hasSize(2);
        }

        @Test
        @DisplayName("Deve rejeitar chave Pix já cadastrada")
        void shouldRejectAlreadyRegisteredPixKey() {
            // Given
            ContaEntity conta = conta(ClienteTipo.PESSOA_FISICA, new BigDecimal("100.00"), "1234567", setOf());
            when(contaRepository.findByCliente_Usuario_Login(LOGIN)).thenReturn(Optional.of(conta));
            when(contaRepository.findByChavesPixContaining("chave-duplicada")).thenReturn(Optional.of(conta));

            // When / Then
            assertThatThrownBy(() -> contaService.cadastrarChavePix(LOGIN, "chave-duplicada"))
                    .isInstanceOf(BaseExceptions.class)
                    .hasMessage(ErrorEnum.CHAVEPIX_JACADASTRADA.getErrorMessage());
            verify(contaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve rejeitar cadastro quando o limite de chaves for atingido")
        void shouldRejectWhenPixLimitIsReached() {
            // Given
            ContaEntity conta = conta(ClienteTipo.PESSOA_FISICA, new BigDecimal("100.00"), "1234567",
                    setOf("a", "b", "c", "d", "e"));
            when(contaRepository.findByCliente_Usuario_Login(LOGIN)).thenReturn(Optional.of(conta));
            when(contaRepository.findByChavesPixContaining("nova-chave")).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> contaService.cadastrarChavePix(LOGIN, "nova-chave"))
                    .isInstanceOf(BaseExceptions.class)
                    .hasMessage(ErrorEnum.LIMITE_CHAVEPIX.getErrorMessage());
            verify(contaRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Consulta de conta")
    class ConsultaDeConta {

        @Test
        @DisplayName("Deve retornar os dados da conta quando ela existir")
        void shouldReturnAccountDataWhenFound() {
            // Given
            ContaEntity conta = conta(ClienteTipo.PESSOA_FISICA, new BigDecimal("123.45"), "9876543", setOf());
            when(contaRepository.findByCliente_Usuario_Login(LOGIN)).thenReturn(Optional.of(conta));

            // When
            ContaResponseDTO response = contaService.consultarDadosConta(LOGIN);

            // Then
            assertThat(response.id()).isEqualTo(CONTA_ID);
            assertThat(response.agencia()).isEqualTo("0001");
            assertThat(response.numeroConta()).isEqualTo("9876543");
            assertThat(response.saldo()).isEqualByComparingTo("123.45");
            assertThat(response.clienteId()).isEqualTo(CLIENTE_ID);
        }

        @Test
        @DisplayName("Deve rejeitar consulta quando a conta não existir")
        void shouldRejectWhenAccountIsMissing() {
            // Given
            when(contaRepository.findByCliente_Usuario_Login(LOGIN)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> contaService.consultarDadosConta(LOGIN))
                    .isInstanceOf(BaseExceptions.class)
                    .hasMessage(ErrorEnum.NUMERO_CONTA_NAO_EXISTE.getErrorMessage());
        }
    }

    private ContaEntity conta(ClienteTipo tipo, BigDecimal saldo, String numeroConta, Set<String> chavesPix) {
        ClientePFEntity cliente = cliente(tipo);
        return ContaEntity.builder()
                .id(CONTA_ID)
                .agencia("0001")
                .numeroConta(numeroConta)
                .saldo(saldo)
                .cliente(cliente)
                .chavesPix(new HashSet<>(chavesPix))
                .build();
    }

    private ClientePFEntity cliente(ClienteTipo tipo) {
        UsuarioEntity usuario = UsuarioEntity.builder()
                .login(LOGIN)
                .senha("senha123")
                .build();

        ClientePFEntity cliente = new ClientePFEntity();
        cliente.setId(CLIENTE_ID);
        cliente.setNomeRazaoSocial("Cliente Teste");
        cliente.setDocumento("11111111111");
        cliente.setEmail("cliente@teste.com");
        cliente.setTelefone("11999999999");
        cliente.setClienteTipo(tipo);
        cliente.setUsuario(usuario);
        usuario.setCliente(cliente);
        return cliente;
    }

    private Set<String> setOf(String... valores) {
        Set<String> chaves = new HashSet<>();
        chaves.addAll(Arrays.asList(valores));
        return chaves;
    }
}
