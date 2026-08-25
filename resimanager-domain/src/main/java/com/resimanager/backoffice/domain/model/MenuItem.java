package com.resimanager.backoffice.domain.model;

import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Elemento de menú: opción de navegación que apunta a un controlador/método
 * dentro de un módulo y una acción.
 */
public record MenuItem(
        Integer menuId,
        Integer opcionId,
        Integer moduloId,
        Integer accionId,
        String nombre,
        String tipo,
        Integer itemPadre,
        Integer orden,
        String controlador,
        String metodo,
        Estatus estatus,
        AuditInfo audit
) {

    public MenuItem {
        Objects.requireNonNull(nombre, "nombre es obligatorio");
        Objects.requireNonNull(tipo, "tipo es obligatorio");
        Objects.requireNonNull(estatus, "estatus es obligatorio");
        Objects.requireNonNull(audit, "auditoría es obligatoria");
    }

    public boolean estaActivo() {
        return estatus.esActivo();
    }
}