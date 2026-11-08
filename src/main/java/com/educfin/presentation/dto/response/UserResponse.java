package com.educfin.presentation.dto.response;

import com.educfin.application.dto.UserOutput;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {

    public static UserResponse from(UserOutput output) {
        return new UserResponse(
                output.id(),
                output.name(),
                output.email(),
                output.active(),
                output.createdAt(),
                output.updatedAt()
        );
    }
}
