package com.resimanager.backoffice.domain.model;

import com.resimanager.backoffice.domain.exception.DomainException;
import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Propiedad dentro de un conjunto.
 */
public record Propiedad(
        Integer id,
        Integer conjuntoId,
        Integer claseDePropiedadId,
        String numero,
        BigDecimal cantidad,
        BigDecimal coeficienteParticipacion,
        Estatus estatus,
        AuditInfo audit
) {

    public Propiedad {
        Objects.requireNonNull(conjuntoId, "conjunto es obligatorio");
        Objects.requireNonNull(claseDePropiedadId, "clase de propiedad es obligatoria");
        Objects.requireNonNull(numero, "número es obligatorio");
        Objects.requireNonNull(cantidad, "cantidad es obligatoria");
        Objects.requireNonNull(coeficienteParticipacion, "coeficiente de participación es obligatorio");
        Objects.requireNonNull(estatus, "estatus es obligatorio");
        Objects.requireNonNull(audit, "auditoría es obligatoria");
        if (numero.isBlank()) {
            throw new DomainException("número es obligatorio");
        }
        if (cantidad.signum() < 0 || coeficienteParticipacion.signum() < 0) {
            throw new DomainException("cantidad y coeficiente de participación no pueden ser negativos");
        }
    }

    public boolean estaActivo() {
        return estatus.esActivo();
    }
}