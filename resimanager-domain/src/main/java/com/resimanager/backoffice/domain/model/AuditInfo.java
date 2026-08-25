package com.resimanager.backoffice.domain.model;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Auditoría de una entidad de dominio: quién, cuándo y desde dónde se creó
 * y se modificó por última vez.
 */
public record AuditInfo(
        String creadoPor,
        OffsetDateTime creadoEn,
        String estacionCreador,
        String modificadoPor,
        OffsetDateTime modificadoEn,
        String estacionModificador
) {

    public AuditInfo {
        Objects.requireNonNull(creadoPor, "creadoPor es obligatorio");
        Objects.requireNonNull(creadoEn, "creadoEn es obligatorio");
        Objects.requireNonNull(estacionCreador, "estacionCreador es obligatorio");
    }

    public static AuditInfo deCreacion(String usuario, String estacion, OffsetDateTime ahora) {
        Objects.requireNonNull(usuario, "usuario es obligatorio");
        Objects.requireNonNull(estacion, "estacion es obligatorio");
        Objects.requireNonNull(ahora, "ahora es obligatorio");
        return new AuditInfo(usuario, ahora, estacion, usuario, ahora, estacion);
    }

    public AuditInfo modificadoPor(String usuario, String estacion, OffsetDateTime ahora) {
        Objects.requireNonNull(usuario, "usuario es obligatorio");
        Objects.requireNonNull(estacion, "estacion es obligatorio");
        Objects.requireNonNull(ahora, "ahora es obligatorio");
        return new AuditInfo(creadoPor, creadoEn, estacionCreador, usuario, ahora, estacion);
    }
}