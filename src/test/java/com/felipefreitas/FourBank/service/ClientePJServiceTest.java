package com.felipefreitas.FourBank.service;

import com.felipefreitas.FourBank.dto.cliente.ClientePJRequestDTO;
import com.felipefreitas.FourBank.dto.cliente.ClientePJResponseDTO;
import com.felipefreitas.FourBank.dto.endereco.EnderecoRequestDTO;
import com.felipefreitas.FourBank.dto.usuario.UsuarioRequestDTO;
import com.felipefreitas.FourBank.entity.ClientePJEntity;
import com.felipefreitas.FourBank.entity.UsuarioEntity;
import com.felipefreitas.FourBank.enums.ClienteTipo;
import com.felipefreitas.FourBank.enums.ErrorEnum;
import com.felipefreitas.FourBank.enums.StatusCliente;
import com.felipefreitas.FourBank.exceptions.BaseExceptions;
import com.felipefreitas.FourBank.repository.ClientePJRepository;
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
import java.time.LocalDate;
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
@DisplayName("ClientePJService - comportamento de cadastro de pessoa jurídica")
class ClientePJServiceTest {

    private static final String CNPJ_VALIDO = "11222333000181";
    private static final String EMAIL = "empresa@example.com";
    private static final String LOGIN = "empresa.login";
    private static final String SENHA = "senha123";
    private static final UUID CLIENTE_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID ENDERECO_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID USUARIO_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Mock
    private ClientePJRepository clientePJRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private ClientePJService service;

    @BeforeEach
    void setUp() {
        service = new ClientePJService(clientePJRepository, usuarioRepository, passwordEncoder);
    }

    @Nested
    @DisplayName("Cenários de sucesso")
    class SuccessScenarios {

        @Test
        @DisplayName("Deve cadastrar cliente PJ e usuário autenticável")
        void shouldRegisterClientPjAndUser() {
            // Given
            ClientePJRequestDTO request = requestValido();
            when(clientePJRepository.existsByDocumento(CNPJ_VALIDO)).thenReturn(false);
            when(clientePJRepository.existsByEmail(EMAIL)).thenReturn(false);
            when(usuarioRepository.findByLogin(LOGIN)).thenReturn(Optional.empty());
            when(passwordEncoder.encode(SENHA)).thenReturn("encoded-password");
            when(clientePJRepository.save(any(ClientePJEntity.class))).thenAnswer(invocation -> {
                ClientePJEntity cliente = invocation.getArgument(0);
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
            ClientePJResponseDTO response = service.cadastroClientePJ(request);

            // Then
            ArgumentCaptor<ClientePJEntity> clienteCaptor = ArgumentCaptor.forClass(ClientePJEntity.class);
            ArgumentCaptor<UsuarioEntity> usuarioCaptor = ArgumentCaptor.forClass(UsuarioEntity.class);
            verify(clientePJRepository).save(clienteCaptor.capture());
            verify(usuarioRepository).save(usuarioCaptor.capture());

            assertThat(response.id()).isEqualTo(CLIENTE_ID);
            assertThat(response.razaoSocial()).isEqualTo("Empresa LTDA");
            assertThat(response.nomeFantasia()).isEqualTo("Empresa Teste");
            assertThat(response.dataFundacao()).isEqualTo(LocalDate.of(2000, 1, 1));
            assertThat(response.faturamentoAnual()).isEqualByComparingTo("123456.78");
            assertThat(response.cnpj()).isEqualTo(CNPJ_VALIDO);
            assertThat(response.email()).isEqualTo(EMAIL);
            assertThat(response.telefone()).isEqualTo("11988887777");
            assertThat(response.statusCliente()).isEqualTo(StatusCliente.ATIVO);
            assertThat(response.clienteTipo()).isEqualTo(ClienteTipo.PESSOA_JURIDICA);
            assertThat(response.endereco().id()).isEqualTo(ENDERECO_ID);
            assertThat(response.endereco().cidade()).isEqualTo("São Paulo");
            assertThat(response.usuario().id()).isEqualTo(USUARIO_ID);
            assertThat(response.usuario().login()).isEqualTo(LOGIN);

            assertThat(clienteCaptor.getValue().getStatusCliente()).isEqualTo(StatusCliente.ATIVO);
            assertThat(clienteCaptor.getValue().getClienteTipo()).isEqualTo(ClienteTipo.PESSOA_JURIDICA);
            assertThat(clienteCaptor.getValue().getEndereco().getId()).isEqualTo(ENDERECO_ID);
            assertThat(usuarioCaptor.getValue().getSenha()).isEqualTo("encoded-password");
            assertThat(usuarioCaptor.getValue().getCliente()).isSameAs(clienteCaptor.getValue());
        }
    }

    @Nested
    @DisplayName("Validações e falhas")
    class ValidationFailures {

        @Test
        @DisplayName("Deve rejeitar CNPJ inválido")
        void shouldRejectInvalidCnpj() {
            // Given
            ClientePJRequestDTO request = new ClientePJRequestDTO(
                    "Empresa LTDA",
                    "Empresa Teste",
                    LocalDate.of(2000, 1, 1),
                    new BigDecimal("123456.78"),
                    "123",
                    EMAIL,
                    "11988887777",
                    endereco(),
                    usuario());

            // When / Then
            assertThatThrownBy(() -> service.cadastroClientePJ(request))
                    .isInstanceOf(BaseExceptions.class)
                    .hasMessage(ErrorEnum.CNPJ_INVALIDO.getErrorMessage());
            verifyNoInteractions(clientePJRepository, usuarioRepository, passwordEncoder);
        }

        @Test
        @DisplayName("Deve rejeitar CNPJ já cadastrado")
        void shouldRejectAlreadyRegisteredCnpj() {
            // Given
            ClientePJRequestDTO request = requestValido();
            when(clientePJRepository.existsByDocumento(CNPJ_VALIDO)).thenReturn(true);

            // When / Then
            assertThatThrownBy(() -> service.cadastroClientePJ(request))
                    .isInstanceOf(BaseExceptions.class)
                    .hasMessage(ErrorEnum.CLIENTE_JA_CADASTRADO.getErrorMessage());
            verify(clientePJRepository).existsByDocumento(CNPJ_VALIDO);
            verifyNoMoreInteractions(clientePJRepository);
            verifyNoInteractions(usuarioRepository, passwordEncoder);
        }

        @Test
        @DisplayName("Deve rejeitar login já cadastrado")
        void shouldRejectAlreadyRegisteredLogin() {
            // Given
            ClientePJRequestDTO request = requestValido();
            when(clientePJRepository.existsByDocumento(CNPJ_VALIDO)).thenReturn(false);
            when(clientePJRepository.existsByEmail(EMAIL)).thenReturn(false);
            when(usuarioRepository.findByLogin(LOGIN)).thenReturn(Optional.of(UsuarioEntity.builder()
                    .id(USUARIO_ID)
                    .login(LOGIN)
                    .senha("outra")
                    .build()));

            // When / Then
            assertThatThrownBy(() -> service.cadastroClientePJ(request))
                    .isInstanceOf(BaseExceptions.class)
                    .hasMessage(ErrorEnum.LOGIN_JA_CADASTRADO.getErrorMessage());
            verify(clientePJRepository).existsByDocumento(CNPJ_VALIDO);
            verify(clientePJRepository).existsByEmail(EMAIL);
            verify(usuarioRepository).findByLogin(LOGIN);
            verify(clientePJRepository, never()).save(any());
            verify(usuarioRepository, never()).save(any());
        }
    }

    private ClientePJRequestDTO requestValido() {
        return new ClientePJRequestDTO(
                "Empresa LTDA",
                "Empresa Teste",
                LocalDate.of(2000, 1, 1),
                new BigDecimal("123456.78"),
                CNPJ_VALIDO,
                EMAIL,
                "11988887777",
                endereco(),
                usuario());
    }

    private EnderecoRequestDTO endereco() {
        return new EnderecoRequestDTO(
                "Rua B",
                "456",
                "01002000",
                "Centro",
                "São Paulo",
                "SP");
    }

    private UsuarioRequestDTO usuario() {
        return new UsuarioRequestDTO(LOGIN, SENHA);
    }
}
