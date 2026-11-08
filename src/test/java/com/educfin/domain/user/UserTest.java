package com.educfin.domain.user;

import com.educfin.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    private static final Instant NOW = Instant.parse("2026-01-01T10:00:00Z");
    private static final Instant LATER = NOW.plusSeconds(60);
    private static final Email EMAIL = Email.of("maria@example.com");

    private static User newUser() {
        return User.create("Maria", EMAIL, "hash", NOW);
    }

    @Nested
    class Create {

        @Test
        @DisplayName("cria usuário ativo, com id gerado e timestamps iguais")
        void createsActiveUser() {
            User user = newUser();

            assertThat(user.getId()).isNotNull();
            assertThat(user.getName()).isEqualTo("Maria");
            assertThat(user.getEmail()).isEqualTo(EMAIL);
            assertThat(user.getPasswordHash()).isEqualTo("hash");
            assertThat(user.isActive()).isTrue();
            assertThat(user.getCreatedAt()).isEqualTo(NOW);
            assertThat(user.getUpdatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("gera ids diferentes a cada criação")
        void generatesUniqueIds() {
            assertThat(newUser().getId()).isNotEqualTo(newUser().getId());
        }

        @Test
        @DisplayName("remove espaços do nome")
        void trimsName() {
            User user = User.create("  Maria  ", EMAIL, "hash", NOW);

            assertThat(user.getName()).isEqualTo("Maria");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("rejeita nome em branco")
        void rejectsBlankName(String name) {
            assertThatThrownBy(() -> User.create(name, EMAIL, "hash", NOW))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Nome é obrigatório");
        }

        @Test
        @DisplayName("rejeita nome acima de 120 caracteres")
        void rejectsLongName() {
            assertThatThrownBy(() -> User.create("a".repeat(121), EMAIL, "hash", NOW))
                    .isInstanceOf(DomainException.class);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("rejeita hash de senha vazio")
        void rejectsBlankHash(String hash) {
            assertThatThrownBy(() -> User.create("Maria", EMAIL, hash, NOW))
                    .isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("rejeita e-mail nulo")
        void rejectsNullEmail() {
            assertThatThrownBy(() -> User.create("Maria", null, "hash", NOW))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    class Behavior {

        @Test
        @DisplayName("rename altera o nome e o updatedAt")
        void rename() {
            User user = newUser();

            user.rename("Maria Silva", LATER);

            assertThat(user.getName()).isEqualTo("Maria Silva");
            assertThat(user.getUpdatedAt()).isEqualTo(LATER);
            assertThat(user.getCreatedAt()).isEqualTo(NOW);
        }

        @Test
        @DisplayName("rename valida o novo nome")
        void renameValidates() {
            User user = newUser();

            assertThatThrownBy(() -> user.rename(" ", LATER)).isInstanceOf(DomainException.class);
            assertThat(user.getName()).isEqualTo("Maria");
        }

        @Test
        @DisplayName("changePasswordHash altera o hash e o updatedAt")
        void changePasswordHash() {
            User user = newUser();

            user.changePasswordHash("novo-hash", LATER);

            assertThat(user.getPasswordHash()).isEqualTo("novo-hash");
            assertThat(user.getUpdatedAt()).isEqualTo(LATER);
        }

        @Test
        @DisplayName("deactivate/activate alternam o status")
        void toggleActive() {
            User user = newUser();

            user.deactivate(LATER);
            assertThat(user.isActive()).isFalse();
            assertThat(user.getUpdatedAt()).isEqualTo(LATER);

            user.activate(LATER.plusSeconds(1));
            assertThat(user.isActive()).isTrue();
        }
    }

    @Nested
    class Identity {

        @Test
        @DisplayName("igualdade é baseada apenas no id")
        void equalityById() {
            User original = newUser();
            User restored = User.restore(original.getId(), "Outro nome", Email.of("outro@example.com"),
                    "outro-hash", false, NOW, LATER);

            assertThat(restored).isEqualTo(original).hasSameHashCodeAs(original);
            assertThat(newUser()).isNotEqualTo(original);
        }
    }
}
