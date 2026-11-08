package com.educfin.domain.user;

import com.educfin.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasswordPolicyTest {

    @ParameterizedTest
    @ValueSource(strings = {"Senha123", "abcdefg1", "1234567a", "frase longa com espaços 99"})
    @DisplayName("aceita senhas válidas")
    void acceptsValid(String password) {
        assertThatCode(() -> PasswordPolicy.validate(password)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("aceita exatamente o tamanho máximo")
    void acceptsMaxLength() {
        String password = "a1" + "x".repeat(PasswordPolicy.MAX_LENGTH - 2);

        assertThatCode(() -> PasswordPolicy.validate(password)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "abc1", "Senha12"})
    @DisplayName("rejeita senhas nulas ou curtas")
    void rejectsShort(String password) {
        assertThatThrownBy(() -> PasswordPolicy.validate(password))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("entre");
    }

    @Test
    @DisplayName("rejeita senhas acima do limite do BCrypt")
    void rejectsTooLong() {
        String password = "a1" + "x".repeat(PasswordPolicy.MAX_LENGTH);

        assertThatThrownBy(() -> PasswordPolicy.validate(password))
                .isInstanceOf(DomainException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"somenteletras", "12345678"})
    @DisplayName("exige ao menos uma letra e um número")
    void requiresLetterAndDigit(String password) {
        assertThatThrownBy(() -> PasswordPolicy.validate(password))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("letra e um número");
    }
}
