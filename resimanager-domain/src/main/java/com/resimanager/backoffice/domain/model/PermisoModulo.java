package com.resimanager.backoffice.domain.model;

/**
 * Permiso de un perfil sobre un módulo/acción.
 */
public record PermisoModulo(
        Integer moduloId,
        String moduloNombre,
        String accionNombre
) {}