package com.resimanager.backoffice.domain.model;

import com.resimanager.backoffice.domain.exception.DomainException;
import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Perfil de acceso del sistema.
 * Define el conjunto de módulos a los que un usuario tiene acceso dentro de un contexto.
 */
public record Perfil(
        Integer id,
        String nombre,
        String descripcion,
        Integer nivel,
        Estatus estatus,
        AuditInfo audit
) {

    public Perfil {
        Objects.requireNonNull(nombre, "nombre es obligatorio");
        Objects.requireNonNull(nivel, "nivel es obligatorio");
        Objects.requireNonNull(estatus, "estatus es obligatorio");
        Objects.requireNonNull(audit, "auditoría es obligatoria");
        if (nombre.isBlank()) {
            throw new DomainException("nombre es obligatorio");
        }
        if (nivel < 1) {
            throw new DomainException("el nivel del perfil debe ser mayor o igual a 1");
        }
    }

    public static Perfil crear(Integer id, String nombre, String descripcion, Integer nivel,
                               String ejecutor, String estacion, OffsetDateTime ahora) {
        return new Perfil(id, nombre, descripcion, nivel, Estatus.ACTIVO,
                AuditInfo.deCreacion(ejecutor, estacion, ahora));
    }

    public Perfil actualizarDatos(String nombre, String descripcion, Integer nivel,
                                  String ejecutor, String estacion, OffsetDateTime ahora) {
        return new Perfil(id, nombre, descripcion, nivel, estatus,
                audit.modificadoPor(ejecutor, estacion, ahora));
    }

    public Perfil cambiarEstatus(Estatus nuevoEstatus, String ejecutor, String estacion, OffsetDateTime ahora) {
        return new Perfil(id, nombre, descripcion, nivel, nuevoEstatus,
                audit.modificadoPor(ejecutor, estacion, ahora));
    }

    public boolean estaActivo() {
        return estatus.esActivo();
    }
}