package com.educfin.application.usecase;

import com.educfin.application.dto.UserOutput;
import com.educfin.application.exception.EmailAlreadyInUseException;
import com.educfin.application.fakes.FakePasswordHasher;
import com.educfin.application.fakes.InMemoryUserRepository;
import com.educfin.application.port.in.RegisterUserUseCase;
import com.educfin.domain.exception.DomainException;
import com.educfin.domain.user.Email;
import com.educfin.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegisterUserServiceTest {

    private static final Instant NOW = Instant.parse("2026-01-01T12:00:00Z");

    private InMemoryUserRepository repository;
    private RegisterUserUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
        useCase = new RegisterUserService(repository, new FakePasswordHasher(), Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    @DisplayName("cadastra usuário com e-mail normalizado e timestamps do Clock")
    void registersUser() {
        UserOutput output = useCase.execute(new RegisterUserUseCase.Input("Maria", " Maria@Example.com ", "Senha123"));

        assertThat(output.id()).isNotNull();
        assertThat(output.name()).isEqualTo("Maria");
        assertThat(output.email()).isEqualTo("maria@example.com");
        assertThat(output.active()).isTrue();
        assertThat(output.createdAt()).isEqualTo(NOW);
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("persiste apenas o hash da senha, nunca a senha pura")
    void storesHashedPassword() {
        useCase.execute(new RegisterUserUseCase.Input("Maria", "maria@example.com", "Senha123"));

        User saved = repository.findByEmail(Email.of("maria@example.com")).orElseThrow();
        assertThat(saved.getPasswordHash()).isEqualTo("hashed:Senha123").isNotEqualTo("Senha123");
    }

    @Test
    @DisplayName("rejeita e-mail já cadastrado (ignorando caixa)")
    void rejectsDuplicateEmail() {
        useCase.execute(new RegisterUserUseCase.Input("Maria", "maria@example.com", "Senha123"));

        assertThatThrownBy(() -> useCase.execute(
                new RegisterUserUseCase.Input("Outra", "MARIA@example.com", "Senha456")))
                .isInstanceOf(EmailAlreadyInUseException.class);
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("rejeita senha fraca sem persistir nada")
    void rejectsWeakPassword() {
        assertThatThrownBy(() -> useCase.execute(
                new RegisterUserUseCase.Input("Maria", "maria@example.com", "fraca")))
                .isInstanceOf(DomainException.class);
        assertThat(repository.count()).isZero();
    }

    @Test
    @DisplayName("rejeita e-mail inválido")
    void rejectsInvalidEmail() {
        assertThatThrownBy(() -> useCase.execute(
                new RegisterUserUseCase.Input("Maria", "invalido", "Senha123")))
                .isInstanceOf(DomainException.class);
        assertThat(repository.count()).isZero();
    }

    @Test
    @DisplayName("rejeita nome em branco")
    void rejectsBlankName() {
        assertThatThrownBy(() -> useCase.execute(
                new RegisterUserUseCase.Input(" ", "maria@example.com", "Senha123")))
                .isInstanceOf(DomainException.class);
        assertThat(repository.count()).isZero();
    }
}
