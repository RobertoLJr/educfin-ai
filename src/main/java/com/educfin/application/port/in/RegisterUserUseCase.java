package com.educfin.application.port.in;

import com.educfin.application.dto.UserOutput;

/**
 * Caso de uso: cadastrar um novo usuário.
 */
public interface RegisterUserUseCase {

    UserOutput execute(Input input);

    record Input(String name, String email, String password) {
    }
}
