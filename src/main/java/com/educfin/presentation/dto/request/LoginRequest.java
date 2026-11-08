package com.educfin.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(example = "maria@example.com")
        @NotBlank
        String email,

        @Schema(example = "Senha123")
        @NotBlank
        String password
) {
}
