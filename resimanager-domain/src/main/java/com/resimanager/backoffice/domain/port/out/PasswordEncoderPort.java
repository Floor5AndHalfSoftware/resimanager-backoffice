package com.resimanager.backoffice.domain.port.out;

/**
 * Puerto de salida de codificación de contraseñas.
 */
public interface PasswordEncoderPort {

    String codificar(String password);

    boolean coincide(String passwordCruda, String hash);
}