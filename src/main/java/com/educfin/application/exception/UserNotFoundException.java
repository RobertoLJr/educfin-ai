package com.educfin.application.exception;

public class UserNotFoundException extends ApplicationException {

    public UserNotFoundException() {
        super("Usuário não encontrado");
    }
}
