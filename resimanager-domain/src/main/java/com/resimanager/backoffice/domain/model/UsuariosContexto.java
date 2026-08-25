package com.resimanager.backoffice.domain.model;

import java.util.List;

/**
 * Usuarios de una entidad de contexto (administradora/conjunto) con sus perfiles.
 */
public record UsuariosContexto(
        Integer entidadId,
        String entidadNombre,
        List<UsuarioConPerfiles> usuarios
) {}