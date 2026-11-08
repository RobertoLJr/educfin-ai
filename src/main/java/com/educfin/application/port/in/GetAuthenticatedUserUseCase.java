package com.educfin.application.port.in;

import com.educfin.application.dto.UserOutput;

import java.util.UUID;

/**
 * Caso de uso: obter os dados do usuário autenticado.
 */
public interface GetAuthenticatedUserUseCase {

    UserOutput execute(Input input);

    record Input(UUID userId) {
    }
}
