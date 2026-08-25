package com.resimanager.backoffice.domain.model;

import com.resimanager.backoffice.domain.exception.DomainException;
import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Persona usuaria del sistema (operador).
 * Es el agregado raíz de la gestión de usuarios: encapsula sus datos, su estatus
 * y las operaciones que preservan sus invariantes.
 */
public record Persona(
        Integer id,
        String docIdent,
        String nombre,
        String apellido,
        String telefono,
        String email,
        String usuario,
        String passwordHash,
        Estatus estatus,
        AuditInfo audit
) {

    public Persona {
        Objects.requireNonNull(docIdent, "documento de identidad es obligatorio");
        Objects.requireNonNull(nombre, "nombre es obligatorio");
        Objects.requireNonNull(apellido, "apellido es obligatorio");
        Objects.requireNonNull(estatus, "estatus es obligatorio");
        Objects.requireNonNull(audit, "auditoría es obligatoria");
        if (docIdent.isBlank() || nombre.isBlank() || apellido.isBlank()) {
            throw new DomainException("documento, nombre y apellido son obligatorios");
        }
    }

    public static Persona crear(Integer id, String docIdent, String nombre, String apellido,
                                String telefono, String email, String usuario, String passwordHash,
                                String ejecutor, String estacion, OffsetDateTime ahora) {
        return new Persona(id, docIdent, nombre, apellido, telefono, email, usuario, passwordHash,
                Estatus.ACTIVO, AuditInfo.deCreacion(ejecutor, estacion, ahora));
    }

    public Persona actualizarDatos(String nombre, String apellido, String telefono, String email,
                                   String ejecutor, String estacion, OffsetDateTime ahora) {
        return new Persona(id, docIdent, nombre, apellido, telefono, email, usuario, passwordHash,
                estatus, audit.modificadoPor(ejecutor, estacion, ahora));
    }

    public Persona cambiarEstatus(Estatus nuevoEstatus, String ejecutor, String estacion, OffsetDateTime ahora) {
        Objects.requireNonNull(nuevoEstatus, "nuevo estatus es obligatorio");
        return new Persona(id, docIdent, nombre, apellido, telefono, email, usuario, passwordHash,
                nuevoEstatus, audit.modificadoPor(ejecutor, estacion, ahora));
    }

    public Persona inactivar(String ejecutor, String estacion, OffsetDateTime ahora) {
        return cambiarEstatus(Estatus.INACTIVO, ejecutor, estacion, ahora);
    }

    public boolean estaActiva() {
        return estatus.esActivo();
    }

    public String nombreCompleto() {
        return nombre + " " + apellido;
    }
}