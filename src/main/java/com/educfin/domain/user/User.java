package com.educfin.domain.user;

import com.educfin.domain.exception.DomainException;

import java.time.Instant;
import java.util.Objects;

/**
 * Entidade de domínio Usuário.
 * <p>
 * Não possui dependência de framework (JPA, Spring, etc.). As invariantes
 * são garantidas na construção e nos métodos de comportamento.
 */
public class User {

    private static final int NAME_MAX_LENGTH = 120;

    private final UserId id;
    private String name;
    private final Email email;
    private String passwordHash;
    private boolean active;
    private final Instant createdAt;
    private Instant updatedAt;

    private User(UserId id, String name, Email email, String passwordHash,
                 boolean active, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "id é obrigatório");
        this.name = validateName(name);
        this.email = Objects.requireNonNull(email, "email é obrigatório");
        this.passwordHash = validatePasswordHash(passwordHash);
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt é obrigatório");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt é obrigatório");
    }

    /**
     * Cria um novo usuário (ainda não persistido).
     */
    public static User create(String name, Email email, String passwordHash, Instant now) {
        return new User(UserId.generate(), name, email, passwordHash, true, now, now);
    }

    /**
     * Reconstitui um usuário existente (ex.: a partir do banco de dados).
     */
    public static User restore(UserId id, String name, Email email, String passwordHash,
                               boolean active, Instant createdAt, Instant updatedAt) {
        return new User(id, name, email, passwordHash, active, createdAt, updatedAt);
    }

    public void rename(String newName, Instant now) {
        this.name = validateName(newName);
        this.updatedAt = now;
    }

    public void changePasswordHash(String newPasswordHash, Instant now) {
        this.passwordHash = validatePasswordHash(newPasswordHash);
        this.updatedAt = now;
    }

    public void deactivate(Instant now) {
        this.active = false;
        this.updatedAt = now;
    }

    public void activate(Instant now) {
        this.active = true;
        this.updatedAt = now;
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new DomainException("Nome é obrigatório");
        }
        String trimmed = name.trim();
        if (trimmed.length() > NAME_MAX_LENGTH) {
            throw new DomainException("Nome deve ter no máximo " + NAME_MAX_LENGTH + " caracteres");
        }
        return trimmed;
    }

    private static String validatePasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new DomainException("Hash de senha é obrigatório");
        }
        return passwordHash;
    }

    public UserId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Email getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
