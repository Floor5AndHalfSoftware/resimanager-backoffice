package com.resimanager.backoffice.domain.port.in;

import com.resimanager.backoffice.domain.model.AuthUser;

/**
 * Casos de uso de autenticación.
 */
public interface AuthUseCase {

    /**
     * Carga un usuario por nombre de usuario o email para el proceso de autenticación,
     * junto con las authorities derivadas de sus perfiles.
     *
     * @param usuarioOEmail nombre de usuario o email
     * @return usuario autenticable
     * @throws com.resimanager.backoffice.domain.exception.DomainException si el usuario no existe o está inactivo
     */
    AuthUser cargarUsuarioAutenticable(String usuarioOEmail);
}