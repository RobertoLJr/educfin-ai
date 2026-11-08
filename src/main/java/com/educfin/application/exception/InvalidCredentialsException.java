package com.educfin.application.exception;

public class InvalidCredentialsException extends ApplicationException {

    public InvalidCredentialsException() {
        super("Credenciais inválidas");
    }
}
