package com.educfin.application.usecase;

import com.educfin.application.dto.AuthTokens;
import com.educfin.application.exception.InvalidCredentialsException;
import com.educfin.application.fakes.FakePasswordHasher;
import com.educfin.application.fakes.FakeTokenProvider;
import com.educfin.application.fakes.InMemoryUserRepository;
import com.educfin.application.port.in.AuthenticateUserUseCase;
import com.educfin.domain.user.Email;
import com.educfin.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthenticateUserServiceTest {

    private static final Instant NOW = Instant.parse("2026-01-01T12:00:00Z");

    private InMemoryUserRepository repository;
    private AuthenticateUserUseCase useCase;
    private User maria;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
        FakePasswordHasher hasher = new FakePasswordHasher();
        useCase = new AuthenticateUserService(repository, hasher, new FakeTokenProvider());

        maria = repository.save(User.create("Maria", Email.of("maria@example.com"), hasher.hash("Senha123"), NOW));
    }

    @Test
    @DisplayName("retorna tokens para credenciais válidas")
    void authenticates() {
        AuthTokens tokens = useCase.execute(new AuthenticateUserUseCase.Input("maria@example.com", "Senha123"));

        assertThat(tokens.accessToken()).isEqualTo(FakeTokenProvider.accessTokenFor(maria.getId()));
        assertThat(tokens.refreshToken()).isEqualTo(FakeTokenProvider.refreshTokenFor(maria.getId()));
        assertThat(tokens.accessTokenExpiresIn()).isEqualTo(FakeTokenProvider.ACCESS_TTL);
    }

    @Test
    @DisplayName("e-mail é case-insensitive no login")
    void emailIsCaseInsensitive() {
        AuthTokens tokens = useCase.execute(new AuthenticateUserUseCase.Input("MARIA@Example.com", "Senha123"));

        assertThat(tokens).isNotNull();
    }

    @Test
    @DisplayName("rejeita senha incorreta")
    void rejectsWrongPassword() {
        assertThatThrownBy(() -> useCase.execute(new AuthenticateUserUseCase.Input("maria@example.com", "Errada123")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("rejeita usuário inexistente com a mesma exceção (evita enumeração)")
    void rejectsUnknownUser() {
        assertThatThrownBy(() -> useCase.execute(new AuthenticateUserUseCase.Input("joao@example.com", "Senha123")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("rejeita usuário inativo com a mesma exceção")
    void rejectsInactiveUser() {
        maria.deactivate(NOW);
        repository.save(maria);

        assertThatThrownBy(() -> useCase.execute(new AuthenticateUserUseCase.Input("maria@example.com", "Senha123")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"nao-e-email"})
    @DisplayName("e-mail malformado vira credencial inválida (não erro de validação)")
    void rejectsMalformedEmail(String email) {
        assertThatThrownBy(() -> useCase.execute(new AuthenticateUserUseCase.Input(email, "Senha123")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    @DisplayName("senha nula é rejeitada")
    void rejectsNullPassword() {
        assertThatThrownBy(() -> useCase.execute(new AuthenticateUserUseCase.Input("maria@example.com", null)))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
