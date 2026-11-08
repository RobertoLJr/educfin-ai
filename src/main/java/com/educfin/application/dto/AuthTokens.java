package com.educfin.application.dto;

/**
 * Par de tokens emitido após autenticação ou refresh.
 *
 * @param accessToken           token de acesso (curta duração)
 * @param refreshToken          token de renovação (longa duração)
 * @param accessTokenExpiresIn  validade do access token em segundos
 * @param refreshTokenExpiresIn validade do refresh token em segundos
 */
public record AuthTokens(
        String accessToken,
        String refreshToken,
        long accessTokenExpiresIn,
        long refreshTokenExpiresIn
) {
}
