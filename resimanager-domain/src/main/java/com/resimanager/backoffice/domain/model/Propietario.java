package com.resimanager.backoffice.domain.model;

import com.resimanager.backoffice.domain.exception.DomainException;
import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Vinculación de una persona como propietario de una propiedad.
 * Tiene un período de vigencia ({@code fchDesde} a {@code fchHasta}).
 */
public record Propietario(
        Integer conjuntoId,
        Integer personaId,
        Integer propiedadId,
        LocalDate fchDesde,
        LocalDate fchHasta,
        Estatus estatus,
        AuditInfo audit
) {

    public Propietario {
        Objects.requireNonNull(conjuntoId, "conjunto es obligatorio");
        Objects.requireNonNull(personaId, "persona es obligatorio");
        Objects.requireNonNull(propiedadId, "propiedad es obligatoria");
        Objects.requireNonNull(fchDesde, "fecha desde es obligatoria");
        Objects.requireNonNull(estatus, "estatus es obligatorio");
        Objects.requireNonNull(audit, "auditoría es obligatoria");
        if (fchHasta != null && fchHasta.isBefore(fchDesde)) {
            throw new DomainException("la fecha hasta no puede ser anterior a la fecha desde");
        }
    }

    public boolean estaActivo() {
        return estatus.esActivo();
    }
}