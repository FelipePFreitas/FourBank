package com.felipefreitas.FourBank.service;

import com.felipefreitas.FourBank.dto.auth.AuthTokenResponseDTO;
import com.felipefreitas.FourBank.dto.auth.LoginRequestDTO;
import com.felipefreitas.FourBank.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - autenticação e geração de token")
class AuthServiceTest {

    private static final String LOGIN = "usuario.login";
    private static final String SENHA = "senha123";

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    private AuthService service;

    @BeforeEach
    void setUp() {
        service = new AuthService(authenticationManager, jwtService);
    }

    @Nested
    @DisplayName("Cenários de sucesso")
    class SuccessScenarios {

        @Test
        @DisplayName("Deve autenticar com sucesso e retornar Bearer token")
        void shouldAuthenticateSuccessfullyAndReturnBearerToken() {
            // Given
            LoginRequestDTO request = new LoginRequestDTO(LOGIN, SENHA);
            UserDetails principal = User.withUsername(LOGIN)
                    .password(SENHA)
                    .authorities("ROLE_USER")
                    .build();
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    principal, SENHA, principal.getAuthorities());

            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(jwtService.generateToken(principal)).thenReturn("jwt-token");
            when(jwtService.getExpirationMillis()).thenReturn(3600000L);

            // When
            AuthTokenResponseDTO response = service.authenticate(request);

            // Then
            ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                    ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
            verify(authenticationManager).authenticate(captor.capture());
            assertThat(captor.getValue().getPrincipal()).isEqualTo(LOGIN);
            assertThat(captor.getValue().getCredentials()).isEqualTo(SENHA);
            verify(jwtService).generateToken(principal);
            assertThat(response.tokenType()).isEqualTo("Bearer");
            assertThat(response.accessToken()).isEqualTo("jwt-token");
            assertThat(response.expiresInMillis()).isEqualTo(3600000L);
        }
    }

    @Nested
    @DisplayName("Falhas de autenticação")
    class AuthenticationFailures {

        @Test
        @DisplayName("Deve propagar erro quando o AuthenticationManager rejeitar as credenciais")
        void shouldPropagateErrorWhenCredentialsAreRejected() {
            // Given
            LoginRequestDTO request = new LoginRequestDTO(LOGIN, SENHA);
            when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("inválido"));

            // When / Then
            assertThatThrownBy(() -> service.authenticate(request))
                    .isInstanceOf(BadCredentialsException.class);
            verify(authenticationManager).authenticate(any());
            verifyNoInteractions(jwtService);
        }
    }
}
