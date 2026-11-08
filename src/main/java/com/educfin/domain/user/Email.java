package com.educfin.domain.user;

import com.educfin.domain.exception.DomainException;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object que garante que um e-mail é sempre válido e normalizado.
 */
public record Email(String value) {

    private static final int MAX_LENGTH = 255;
    private static final Pattern PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public Email {
        Objects.requireNonNull(value, "email não pode ser nulo");
        value = value.trim().toLowerCase(Locale.ROOT);
        if (value.length() > MAX_LENGTH || !PATTERN.matcher(value).matches()) {
            throw new DomainException("E-mail inválido");
        }
    }

    public static Email of(String value) {
        return new Email(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
