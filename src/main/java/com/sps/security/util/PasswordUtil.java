package com.sps.security.util;

import at.favre.lib.crypto.bcrypt.BCrypt;

public final class PasswordUtil {

    private PasswordUtil() {
    }

    public static String hash(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "La contraseña no puede estar vacía."
            );
        }

        return BCrypt.withDefaults()
                .hashToString(12, password.toCharArray());
    }

    public static boolean verificar(
            String passwordPlano,
            String hash
    ) {
        if (passwordPlano == null
                || passwordPlano.isBlank()
                || hash == null
                || hash.isBlank()) {
            return false;
        }

        return BCrypt.verifyer()
                .verify(
                        passwordPlano.toCharArray(),
                        hash
                )
                .verified;
    }
}