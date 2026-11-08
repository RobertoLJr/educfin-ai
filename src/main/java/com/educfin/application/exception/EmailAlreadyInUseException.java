package com.educfin.application.exception;

public class EmailAlreadyInUseException extends ApplicationException {

    public EmailAlreadyInUseException() {
        super("E-mail já está em uso");
    }
}
