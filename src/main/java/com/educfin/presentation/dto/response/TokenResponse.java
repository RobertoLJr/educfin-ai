package com.educfin.presentation.dto.response;

import com.educfin.application.dto.AuthTokens;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        long refreshExpiresIn
) {

    private static final String BEARER = "Bearer";

    public static TokenResponse from(AuthTokens tokens) {
        return new TokenResponse(
                tokens.accessToken(),
                tokens.refreshToken(),
                BEARER,
                tokens.accessTokenExpiresIn(),
                tokens.refreshTokenExpiresIn()
        );
    }
}
