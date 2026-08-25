package com.resimanager.backoffice.domain.model;

import com.resimanager.backoffice.domain.model.enums.TipoContexto;

import java.util.List;
import java.util.Objects;

/**
 * Contexto de trabajo disponible para un usuario (administradora o conjunto)
 * junto con los perfiles que puede usar dentro de él.
 */
public record ContextoDisponible(
        TipoContexto tipo,
        Integer entidadId,
        String entidadNombre,
        String entidadDocumento,
        String entidadEmail,
        List<Perfil> perfilesDisponibles
) {

    public ContextoDisponible {
        Objects.requireNonNull(tipo, "tipo es obligatorio");
        Objects.requireNonNull(entidadId, "entidadId es obligatorio");
        Objects.requireNonNull(perfilesDisponibles, "perfilesDisponibles es obligatorio");
    }

    public static ContextoDisponible deAdministradora(Integer id, String nombre, String documento,
                                                      String email, List<Perfil> perfiles) {
        return new ContextoDisponible(TipoContexto.ADMINISTRADORA, id, nombre, documento, email, perfiles);
    }

    public static ContextoDisponible deConjunto(Integer id, String nombre, String documento,
                                                String email, List<Perfil> perfiles) {
        return new ContextoDisponible(TipoContexto.CONJUNTO, id, nombre, documento, email, perfiles);
    }
}