package com.resimanager.backoffice.domain.port.out;

import java.util.Set;

/**
 * Puerto de salida de generación y validación de tokens JWT.
 */
public interface JwtPort {

    String generarToken(String username, Integer userId, String nombre, String apellido,
                        String email, String documento, Set<String> roles);

    boolean esTokenValido(String token);

    String obtenerUsuarioDelToken(String token);
}