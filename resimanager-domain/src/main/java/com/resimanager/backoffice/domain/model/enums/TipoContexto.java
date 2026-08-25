package com.resimanager.backoffice.domain.model.enums;

import com.resimanager.backoffice.domain.exception.DomainException;

import java.util.Arrays;

/**
 * Tipo de contexto de trabajo del usuario dentro del sistema.
 * Un usuario opera dentro de una {@code ADMINISTRADORA} o de un {@code CONJUNTO}.
 */
public enum TipoContexto {
    ADMINISTRADORA,
    CONJUNTO;

    public static TipoContexto desdeCodigo(String tipo) {
        return Arrays.stream(values())
                .filter(t -> t.name().equalsIgnoreCase(tipo))
                .findFirst()
                .orElseThrow(() -> new DomainException("Tipo de contexto inválido: " + tipo));
    }
}