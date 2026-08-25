package com.resimanager.backoffice.domain.model;

import com.resimanager.backoffice.domain.exception.DomainException;
import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Administradora (empresa administradora de condominios).
 * Un contexto de trabajo posible para un usuario del sistema.
 */
public record Administradora(
        Integer id,
        String docIdent,
        String nombre,
        String telefono,
        String email,
        Estatus estatus,
        Integer personaContactoId,
        AuditInfo audit
) {

    public Administradora {
        Objects.requireNonNull(estatus, "estatus es obligatorio");
        Objects.requireNonNull(audit, "auditoría es obligatoria");
        if (nombre != null && nombre.isBlank()) {
            throw new DomainException("nombre no puede estar en blanco");
        }
    }

    public static Administradora crear(Integer id, String docIdent, String nombre, String telefono,
                                       String email, Integer personaContactoId,
                                       String ejecutor, String estacion, OffsetDateTime ahora) {
        return new Administradora(id, docIdent, nombre, telefono, email, Estatus.ACTIVO,
                personaContactoId, AuditInfo.deCreacion(ejecutor, estacion, ahora));
    }

    public Administradora actualizarDatos(String docIdent, String nombre, String telefono, String email,
                                          String ejecutor, String estacion, OffsetDateTime ahora) {
        return new Administradora(id, docIdent, nombre, telefono, email, estatus, personaContactoId,
                audit.modificadoPor(ejecutor, estacion, ahora));
    }

    public Administradora cambiarEstatus(Estatus nuevoEstatus, String ejecutor, String estacion, OffsetDateTime ahora) {
        return new Administradora(id, docIdent, nombre, telefono, email, nuevoEstatus, personaContactoId,
                audit.modificadoPor(ejecutor, estacion, ahora));
    }

    public boolean estaActiva() {
        return estatus.esActivo();
    }
}