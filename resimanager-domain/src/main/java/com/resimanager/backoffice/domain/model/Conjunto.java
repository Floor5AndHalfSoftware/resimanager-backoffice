package com.resimanager.backoffice.domain.model;

import com.resimanager.backoffice.domain.exception.DomainException;
import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Conjunto residencial administrado.
 * Un contexto de trabajo posible para un usuario del sistema.
 */
public record Conjunto(
        Integer id,
        String docIdent,
        String nombre,
        String telefono,
        String email,
        BigDecimal area,
        String origen,
        Estatus estatus,
        Integer personaContactoId,
        AuditInfo audit
) {

    public Conjunto {
        Objects.requireNonNull(docIdent, "documento es obligatorio");
        Objects.requireNonNull(nombre, "nombre es obligatorio");
        Objects.requireNonNull(telefono, "teléfono es obligatorio");
        Objects.requireNonNull(email, "email es obligatorio");
        Objects.requireNonNull(estatus, "estatus es obligatorio");
        Objects.requireNonNull(audit, "auditoría es obligatoria");
        if (docIdent.isBlank() || nombre.isBlank() || telefono.isBlank() || email.isBlank()) {
            throw new DomainException("documento, nombre, teléfono y email son obligatorios");
        }
    }

    public static Conjunto crear(Integer id, String docIdent, String nombre, String telefono, String email,
                                 BigDecimal area, String origen, Integer personaContactoId,
                                 String ejecutor, String estacion, OffsetDateTime ahora) {
        return new Conjunto(id, docIdent, nombre, telefono, email, area, origen, Estatus.ACTIVO,
                personaContactoId, AuditInfo.deCreacion(ejecutor, estacion, ahora));
    }

    public Conjunto actualizarDatos(String docIdent, String nombre, String telefono, String email,
                                    Integer personaContactoId, String ejecutor, String estacion, OffsetDateTime ahora) {
        return new Conjunto(id, docIdent, nombre, telefono, email, area, origen, estatus,
                personaContactoId, audit.modificadoPor(ejecutor, estacion, ahora));
    }

    public Conjunto cambiarEstatus(Estatus nuevoEstatus, String ejecutor, String estacion, OffsetDateTime ahora) {
        return new Conjunto(id, docIdent, nombre, telefono, email, area, origen, nuevoEstatus,
                personaContactoId, audit.modificadoPor(ejecutor, estacion, ahora));
    }

    public boolean estaActiva() {
        return estatus.esActivo();
    }
}