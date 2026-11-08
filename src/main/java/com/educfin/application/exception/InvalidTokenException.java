package com.educfin.application.exception;

public class InvalidTokenException extends ApplicationException {

    public InvalidTokenException() {
        super("Token inválido ou expirado");
    }
}
