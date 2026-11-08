package com.educfin.domain.user;

import com.educfin.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    @Test
    @DisplayName("normaliza o e-mail (trim + lowercase)")
    void normalizes() {
        Email email = Email.of("  Maria.Silva@Example.COM ");

        assertThat(email.value()).isEqualTo("maria.silva@example.com");
    }

    @Test
    @DisplayName("e-mails com caixa diferente são iguais")
    void equalityIgnoresCase() {
        assertThat(Email.of("A@B.com")).isEqualTo(Email.of("a@b.com"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "semarroba", "a@b", "@example.com", "a@.com", "a b@example.com"})
    @DisplayName("rejeita formatos inválidos")
    void rejectsInvalid(String value) {
        assertThatThrownBy(() -> Email.of(value))
                .isInstanceOf(DomainException.class)
                .hasMessage("E-mail inválido");
    }

    @Test
    @DisplayName("rejeita e-mail acima de 255 caracteres")
    void rejectsTooLong() {
        String local = "a".repeat(250);

        assertThatThrownBy(() -> Email.of(local + "@example.com"))
                .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("rejeita nulo")
    void rejectsNull() {
        assertThatThrownBy(() -> Email.of(null))
                .isInstanceOf(NullPointerException.class);
    }
}
