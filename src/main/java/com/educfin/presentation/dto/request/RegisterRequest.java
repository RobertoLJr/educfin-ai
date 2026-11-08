package com.educfin.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Schema(example = "Maria Silva")
        @NotBlank @Size(max = 120)
        String name,

        @Schema(example = "maria@example.com")
        @NotBlank @Email @Size(max = 255)
        String email,

        @Schema(example = "Senha123")
        @NotBlank @Size(min = 8, max = 72)
        String password
) {
}
