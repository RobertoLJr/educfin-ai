package com.educfin.application.usecase;

import com.educfin.application.dto.AuthTokens;
import com.educfin.application.exception.InvalidTokenException;
import com.educfin.application.fakes.FakeTokenProvider;
import com.educfin.application.fakes.InMemoryUserRepository;
import com.educfin.application.port.in.RefreshTokenUseCase;
import com.educfin.domain.user.Email;
import com.educfin.domain.user.User;
import com.educfin.domain.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RefreshTokenServiceTest {

    private static final Instant NOW = Instant.parse("2026-01-01T12:00:00Z");

    private InMemoryUserRepository repository;
    private RefreshTokenUseCase useCase;
    private User maria;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
        useCase = new RefreshTokenService(repository, new FakeTokenProvider());
        maria = repository.save(User.create("Maria", Email.of("maria@example.com"), "hash", NOW));
    }

    @Test
    @DisplayName("emite novo par de tokens para refresh token válido")
    void refreshes() {
        AuthTokens tokens = useCase.execute(
                new RefreshTokenUseCase.Input(FakeTokenProvider.refreshTokenFor(maria.getId())));

        assertThat(tokens.accessToken()).isEqualTo(FakeTokenProvider.accessTokenFor(maria.getId()));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"lixo", "refresh:nao-e-uuid"})
    @DisplayName("rejeita token inválido")
    void rejectsInvalidToken(String token) {
        assertThatThrownBy(() -> useCase.execute(new RefreshTokenUseCase.Input(token)))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    @DisplayName("rejeita access token usado como refresh token")
    void rejectsAccessToken() {
        assertThatThrownBy(() -> useCase.execute(
                new RefreshTokenUseCase.Input(FakeTokenProvider.accessTokenFor(maria.getId()))))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    @DisplayName("rejeita token de usuário que não existe mais")
    void rejectsUnknownUser() {
        String token = FakeTokenProvider.refreshTokenFor(UserId.generate());

        assertThatThrownBy(() -> useCase.execute(new RefreshTokenUseCase.Input(token)))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    @DisplayName("rejeita token de usuário inativo")
    void rejectsInactiveUser() {
        maria.deactivate(NOW);
        repository.save(maria);

        assertThatThrownBy(() -> useCase.execute(
                new RefreshTokenUseCase.Input(FakeTokenProvider.refreshTokenFor(maria.getId()))))
                .isInstanceOf(InvalidTokenException.class);
    }
}
