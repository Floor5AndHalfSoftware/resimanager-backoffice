package com.resimanager.backoffice.domain.model;

import com.resimanager.backoffice.domain.exception.DomainException;
import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Módulo funcional del sistema.
 * Los módulos se asignan a perfiles y organizan el menú de navegación.
 */
public record Modulo(
        Integer id,
        String nombre,
        String descripcion,
        Integer nivel,
        Estatus estatus,
        AuditInfo audit
) {

    public Modulo {
        Objects.requireNonNull(nombre, "nombre es obligatorio");
        Objects.requireNonNull(nivel, "nivel es obligatorio");
        Objects.requireNonNull(estatus, "estatus es obligatorio");
        Objects.requireNonNull(audit, "auditoría es obligatoria");
        if (nombre.isBlank()) {
            throw new DomainException("nombre es obligatorio");
        }
        if (nivel < 1) {
            throw new DomainException("el nivel del módulo debe ser mayor o igual a 1");
        }
    }

    public boolean estaActivo() {
        return estatus.esActivo();
    }
}