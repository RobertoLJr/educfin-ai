package com.educfin.application.dto;

import com.educfin.domain.user.User;

import java.time.Instant;
import java.util.UUID;

/**
 * Representação de saída de um usuário (nunca expõe o hash da senha).
 */
public record UserOutput(
        UUID id,
        String name,
        String email,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {

    public static UserOutput from(User user) {
        return new UserOutput(
                user.getId().value(),
                user.getName(),
                user.getEmail().value(),
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
