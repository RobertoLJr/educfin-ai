package com.educfin.domain.exception;

/**
 * Exceção base para violações de regras de negócio/invariantes do domínio.
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}
