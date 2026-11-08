package com.educfin.infrastructure.security.jwt;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

/**
 * Configurações de JWT (prefixo {@code security.jwt}).
 *
 * @param secret          chave HMAC (mínimo 32 caracteres / 256 bits)
 * @param issuer          emissor dos tokens (claim "iss")
 * @param accessTokenTtl  validade do access token
 * @param refreshTokenTtl validade do refresh token
 */
@Validated
@ConfigurationProperties(prefix = "security.jwt")
public record JwtProperties(
        @NotBlank @Size(min = 32, message = "security.jwt.secret deve ter ao menos 32 caracteres") String secret,
        @NotBlank String issuer,
        @NotNull Duration accessTokenTtl,
        @NotNull Duration refreshTokenTtl
) {
}
