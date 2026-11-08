package com.educfin.infrastructure.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BCryptPasswordHasherTest {

    private final BCryptPasswordHasher hasher = new BCryptPasswordHasher();

    @Test
    @DisplayName("gera hash BCrypt diferente da senha pura")
    void hashes() {
        String hash = hasher.hash("Senha123");

        assertThat(hash).isNotEqualTo("Senha123").startsWith("$2a$");
    }

    @Test
    @DisplayName("usa salt aleatório: mesma senha gera hashes diferentes")
    void usesRandomSalt() {
        assertThat(hasher.hash("Senha123")).isNotEqualTo(hasher.hash("Senha123"));
    }

    @Test
    @DisplayName("matches retorna true apenas para a senha correta")
    void matches() {
        String hash = hasher.hash("Senha123");

        assertThat(hasher.matches("Senha123", hash)).isTrue();
        assertThat(hasher.matches("senha123", hash)).isFalse();
        assertThat(hasher.matches("Outra123", hash)).isFalse();
    }

    @Test
    @DisplayName("matches é seguro com valores nulos")
    void nullSafe() {
        assertThat(hasher.matches(null, hasher.hash("Senha123"))).isFalse();
        assertThat(hasher.matches("Senha123", null)).isFalse();
    }
}
