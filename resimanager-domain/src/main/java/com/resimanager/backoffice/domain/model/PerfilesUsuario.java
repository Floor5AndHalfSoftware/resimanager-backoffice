package com.resimanager.backoffice.domain.model;

import java.util.List;

/**
 * Perfiles de un usuario agrupados por contexto (administradora/conjunto).
 */
public record PerfilesUsuario(
        Persona persona,
        List<ContextoPerfil> contextos
) {

    public record ContextoPerfil(String tipo, Integer entidadId, String entidadNombre,
                                 List<PerfilSimple> perfiles) {}

    public record PerfilSimple(Integer id, String nombre) {}
}