package com.resimanager.backoffice.domain.model;

import java.util.Objects;
import java.util.Set;

/**
 * Usuario cargado para el proceso de autenticación.
 * Incluye el hash de la contraseña y las authorities (roles) derivadas de sus perfiles.
 */
public record AuthUser(
        Integer userId,
        String username,
        String passwordHash,
        Set<String> authorities
) {

    public AuthUser {
        Objects.requireNonNull(userId, "userId es obligatorio");
        Objects.requireNonNull(username, "username es obligatorio");
        Objects.requireNonNull(authorities, "authorities es obligatorio");
    }
}