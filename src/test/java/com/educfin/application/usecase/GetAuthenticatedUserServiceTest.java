package com.educfin.application.usecase;

import com.educfin.application.dto.UserOutput;
import com.educfin.application.exception.UserNotFoundException;
import com.educfin.application.fakes.InMemoryUserRepository;
import com.educfin.application.port.in.GetAuthenticatedUserUseCase;
import com.educfin.domain.user.Email;
import com.educfin.domain.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetAuthenticatedUserServiceTest {

    private InMemoryUserRepository repository;
    private GetAuthenticatedUserUseCase useCase;

    @BeforeEach
    void setUp() {
        repository = new InMemoryUserRepository();
        useCase = new GetAuthenticatedUserService(repository);
    }

    @Test
    @DisplayName("retorna os dados do usuário sem expor o hash da senha")
    void returnsUser() {
        User maria = repository.save(
                User.create("Maria", Email.of("maria@example.com"), "hash", Instant.parse("2026-01-01T00:00:00Z")));

        UserOutput output = useCase.execute(new GetAuthenticatedUserUseCase.Input(maria.getId().value()));

        assertThat(output.id()).isEqualTo(maria.getId().value());
        assertThat(output.name()).isEqualTo("Maria");
        assertThat(output.email()).isEqualTo("maria@example.com");
        assertThat(UserOutput.class.getRecordComponents())
                .extracting(c -> c.getName())
                .doesNotContain("passwordHash", "password");
    }

    @Test
    @DisplayName("lança UserNotFoundException quando o usuário não existe")
    void throwsWhenNotFound() {
        assertThatThrownBy(() -> useCase.execute(new GetAuthenticatedUserUseCase.Input(UUID.randomUUID())))
                .isInstanceOf(UserNotFoundException.class);
    }
}
