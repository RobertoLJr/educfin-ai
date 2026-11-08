package com.educfin.application.port.in;

import com.educfin.application.dto.AuthTokens;

/**
 * Caso de uso: renovar os tokens a partir de um refresh token válido.
 */
public interface RefreshTokenUseCase {

    AuthTokens execute(Input input);

    record Input(String refreshToken) {
    }
}
