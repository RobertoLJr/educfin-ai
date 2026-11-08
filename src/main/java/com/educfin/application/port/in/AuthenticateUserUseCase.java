package com.educfin.application.port.in;

import com.educfin.application.dto.AuthTokens;

/**
 * Caso de uso: autenticar um usuário com e-mail e senha.
 */
public interface AuthenticateUserUseCase {

    AuthTokens execute(Input input);

    record Input(String email, String password) {
    }
}
