package com.felipefreitas.FourBank.service;

import com.felipefreitas.FourBank.dto.cliente.ClientePFRequestDTO;
import com.felipefreitas.FourBank.dto.cliente.ClientePFResponseDTO;
import com.felipefreitas.FourBank.dto.endereco.EnderecoRequestDTO;
import com.felipefreitas.FourBank.dto.usuario.UsuarioRequestDTO;
import com.felipefreitas.FourBank.entity.ClientePFEntity;
import com.felipefreitas.FourBank.entity.EnderecosEntity;
import com.felipefreitas.FourBank.entity.UsuarioEntity;
import com.felipefreitas.FourBank.enums.ClienteTipo;
import com.felipefreitas.FourBank.enums.ErrorEnum;
import com.felipefreitas.FourBank.enums.StatusCliente;
import com.felipefreitas.FourBank.exceptions.BaseExceptions;
import com.felipefreitas.FourBank.repository.ClientePFRepository;
import com.felipefreitas.FourBank.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientePFService - comportamento de cadastro de pessoa física")
class ClientePFServiceTest {

    private static final String CPF_VALIDO = "52998224725";
    private static final String EMAIL = "joao@example.com";
    private static final String LOGIN = "joao.login";
    private static final String SENHA = "senha123";
    private static final UUID CLIENTE_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID ENDERECO_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID USUARIO_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Mock
    private ClientePFRepository clientePFRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ContaService contaService;

    private ClientePFService service;

    @BeforeEach
    void setUp() {
        service = new ClientePFService(clientePFRepository, usuarioRepository, passwordEncoder, contaService);
    }

    @Nested
    @DisplayName("Cenários de sucesso")
    class SuccessScenarios {

        @Test
        @DisplayName("Deve cadastrar cliente PF e criar conta inicial")
        void shouldRegisterClientPfAndCreateInitialAccount() {
            // Given
            ClientePFRequestDTO request = requestValido();
            when(clientePFRepository.existsByDocumento(CPF_VALIDO)).thenReturn(false);
            when(clientePFRepository.existsByEmail(EMAIL)).thenReturn(false);
            when(usuarioRepository.findByLogin(LOGIN)).thenReturn(Optional.empty());
            when(passwordEncoder.encode(SENHA)).thenReturn("encoded-password");
            when(clientePFRepository.save(any(ClientePFEntity.class))).thenAnswer(invocation -> {
                ClientePFEntity cliente = invocation.getArgument(0);
                cliente.setId(CLIENTE_ID);
                cliente.getEndereco().setId(ENDERECO_ID);
                return cliente;
            });
            when(usuarioRepository.save(any(UsuarioEntity.class))).thenAnswer(invocation -> {
                UsuarioEntity usuario = invocation.getArgument(0);
                usuario.setId(USUARIO_ID);
                return usuario;
            });

            // When
            ClientePFResponseDTO response = service.cadastroClientePF(request);

            // Then
            ArgumentCaptor<ClientePFEntity> clienteCaptor = ArgumentCaptor.forClass(ClientePFEntity.class);
            ArgumentCaptor<UsuarioEntity> usuarioCaptor = ArgumentCaptor.forClass(UsuarioEntity.class);
            verify(clientePFRepository).save(clienteCaptor.capture());
            verify(usuarioRepository).save(usuarioCaptor.capture());
            verify(contaService).criarConta(clienteCaptor.getValue(), BigDecimal.ZERO);

            assertThat(response.id()).isEqualTo(CLIENTE_ID);
            assertThat(response.nomeRazaoSocial()).isEqualTo("João da Silva");
            assertThat(response.dataNascimento()).isEqualTo("1990-01-01");
            assertThat(response.documento()).isEqualTo(CPF_VALIDO);
            assertThat(response.email()).isEqualTo(EMAIL);
            assertThat(response.telefone()).isEqualTo("11999999999");
            assertThat(response.statusCliente()).isEqualTo(StatusCliente.ATIVO);
            assertThat(response.clienteTipo()).isEqualTo(ClienteTipo.PESSOA_FISICA);
            assertThat(response.endereco().id()).isEqualTo(ENDERECO_ID);
            assertThat(response.endereco().endereco()).isEqualTo("Rua A");
            assertThat(response.usuario().id()).isEqualTo(USUARIO_ID);
            assertThat(response.usuario().login()).isEqualTo(LOGIN);

            assertThat(clienteCaptor.getValue().getStatusCliente()).isEqualTo(StatusCliente.ATIVO);
            assertThat(clienteCaptor.getValue().getClienteTipo()).isEqualTo(ClienteTipo.PESSOA_FISICA);
            assertThat(clienteCaptor.getValue().getEndereco().getId()).isEqualTo(ENDERECO_ID);
            assertThat(usuarioCaptor.getValue().getSenha()).isEqualTo("encoded-password");
            assertThat(usuarioCaptor.getValue().getCliente()).isSameAs(clienteCaptor.getValue());
        }
    }

    @Nested
    @DisplayName("Validações e falhas")
    class ValidationFailures {

        @Test
        @DisplayName("Deve rejeitar CPF inválido")
        void shouldRejectInvalidCpf() {
            // Given
            ClientePFRequestDTO request = new ClientePFRequestDTO(
                    "João da Silva",
                    "1990-01-01",
                    "123",
                    EMAIL,
                    "11999999999",
                    endereco(),
                    usuario());

            // When / Then
            assertThatThrownBy(() -> service.cadastroClientePF(request))
                    .isInstanceOf(BaseExceptions.class)
                    .hasMessage(ErrorEnum.CPF_INVALIDO.getErrorMessage());
            verifyNoInteractions(clientePFRepository, usuarioRepository, passwordEncoder, contaService);
        }

        @Test
        @DisplayName("Deve rejeitar CPF já cadastrado")
        void shouldRejectAlreadyRegisteredCpf() {
            // Given
            ClientePFRequestDTO request = requestValido();
            when(clientePFRepository.existsByDocumento(CPF_VALIDO)).thenReturn(true);

            // When / Then
            assertThatThrownBy(() -> service.cadastroClientePF(request))
                    .isInstanceOf(BaseExceptions.class)
                    .hasMessage(ErrorEnum.CPF_JA_CADASTRADO.getErrorMessage());
            verify(clientePFRepository).existsByDocumento(CPF_VALIDO);
            verifyNoMoreInteractions(clientePFRepository);
            verifyNoInteractions(usuarioRepository, passwordEncoder, contaService);
        }

        @Test
        @DisplayName("Deve rejeitar login já cadastrado")
        void shouldRejectAlreadyRegisteredLogin() {
            // Given
            ClientePFRequestDTO request = requestValido();
            when(clientePFRepository.existsByDocumento(CPF_VALIDO)).thenReturn(false);
            when(clientePFRepository.existsByEmail(EMAIL)).thenReturn(false);
            when(usuarioRepository.findByLogin(LOGIN)).thenReturn(Optional.of(UsuarioEntity.builder()
                    .id(USUARIO_ID)
                    .login(LOGIN)
                    .senha("outra")
                    .build()));

            // When / Then
            assertThatThrownBy(() -> service.cadastroClientePF(request))
                    .isInstanceOf(BaseExceptions.class)
                    .hasMessage(ErrorEnum.LOGIN_JA_CADASTRADO.getErrorMessage());
            verify(clientePFRepository).existsByDocumento(CPF_VALIDO);
            verify(clientePFRepository).existsByEmail(EMAIL);
            verify(usuarioRepository).findByLogin(LOGIN);
            verify(contaService, never()).criarConta(any(), any());
            verify(clientePFRepository, never()).save(any());
            verify(usuarioRepository, never()).save(any());
        }
    }

    private ClientePFRequestDTO requestValido() {
        return new ClientePFRequestDTO(
                "João da Silva",
                "1990-01-01",
                CPF_VALIDO,
                EMAIL,
                "11999999999",
                endereco(),
                usuario());
    }

    private EnderecoRequestDTO endereco() {
        return new EnderecoRequestDTO(
                "Rua A",
                "123",
                "01001000",
                "Centro",
                "São Paulo",
                "SP");
    }

    private UsuarioRequestDTO usuario() {
        return new UsuarioRequestDTO(LOGIN, SENHA);
    }
}
