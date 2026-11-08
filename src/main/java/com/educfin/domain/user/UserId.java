package com.educfin.domain.user;

import java.util.Objects;
import java.util.UUID;

/**
 * Identificador tipado do usuário (evita "primitive obsession" com UUIDs soltos).
 */
public record UserId(UUID value) {

    public UserId {
        Objects.requireNonNull(value, "id não pode ser nulo");
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    public static UserId of(UUID value) {
        return new UserId(value);
    }

    public static UserId of(String value) {
        return new UserId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
