package com.educfin.infrastructure.security.jwt;

/**
 * Tipo do token, gravado na claim {@value #CLAIM}. Impede que um refresh token
 * seja usado como access token (e vice-versa).
 */
public enum TokenType {

    ACCESS("access"),
    REFRESH("refresh");

    public static final String CLAIM = "token_type";

    private final String value;

    TokenType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
