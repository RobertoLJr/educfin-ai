package com.educfin.application.exception;

/**
 * Exceção base da camada de aplicação.
 */
public abstract class ApplicationException extends RuntimeException {

    protected ApplicationException(String message) {
        super(message);
    }
}
