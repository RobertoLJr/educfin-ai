package com.educfin.application.fakes;

import com.educfin.application.port.out.PasswordHasher;

/**
 * Hasher determinístico e trivial, apenas para testes.
 */
public class FakePasswordHasher implements PasswordHasher {

    private static final String PREFIX = "hashed:";

    @Override
    public String hash(String rawPassword) {
        return PREFIX + rawPassword;
    }

    @Override
    public boolean matches(String rawPassword, String passwordHash) {
        return passwordHash != null && passwordHash.equals(PREFIX + rawPassword);
    }
}
