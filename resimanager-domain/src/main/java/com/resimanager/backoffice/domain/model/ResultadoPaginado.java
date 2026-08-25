package com.resimanager.backoffice.domain.model;

import java.util.List;
import java.util.Objects;

/**
 * Resultado paginado de una consulta de listado.
 *
 * @param <T> tipo de los elementos
 */
public record ResultadoPaginado<T>(List<T> datos, long total, int pagina, int limite) {

    public ResultadoPaginado {
        Objects.requireNonNull(datos, "datos es obligatorio");
        if (pagina < 1) {
            throw new IllegalArgumentException("pagina debe ser mayor o igual a 1");
        }
        if (limite < 1) {
            throw new IllegalArgumentException("limite debe ser mayor o igual a 1");
        }
    }
}