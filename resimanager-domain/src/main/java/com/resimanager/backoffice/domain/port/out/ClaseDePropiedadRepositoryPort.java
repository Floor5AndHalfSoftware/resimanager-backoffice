package com.resimanager.backoffice.domain.port.out;

import com.resimanager.backoffice.domain.model.ClaseDePropiedadOpcion;

import java.util.List;

/**
 * Puerto de salida del catálogo de clases de propiedad.
 */
public interface ClaseDePropiedadRepositoryPort {

    List<ClaseDePropiedadOpcion> listarActivas();
}