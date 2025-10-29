package com.certify.backend.util;

import java.security.SecureRandom;

public class GeneradorContrasena {

    private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
    private static final SecureRandom random = new SecureRandom();

    /**
     * Genera una contraseña temporal aleatoria de 12 caracteres.
     * Incluye: mayúsculas, minúsculas, números y símbolos.
     */
    public static String generarTemporal() {
        StringBuilder sb = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            sb.append(CARACTERES.charAt(random.nextInt(CARACTERES.length())));
        }
        return sb.toString();
    }
}