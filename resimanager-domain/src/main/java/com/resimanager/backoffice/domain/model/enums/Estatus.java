package com.resimanager.backoffice.domain.model.enums;

import com.resimanager.backoffice.domain.exception.DomainException;

import java.util.Arrays;

/**
 * Estatus de registro de las entidades del sistema.
 * Se persiste como {@code A} (activo) o {@code I} (inactivo).
 */
public enum Estatus {
    ACTIVO("A"),
    INACTIVO("I");

    private final String codigo;

    Estatus(String codigo) {
        this.codigo = codigo;
    }

    public String codigo() {
        return codigo;
    }

    public boolean esActivo() {
        return this == ACTIVO;
    }

    public boolean esInactivo() {
        return this == INACTIVO;
    }

    public static Estatus desdeCodigo(String codigo) {
        return Arrays.stream(values())
                .filter(e -> e.codigo.equalsIgnoreCase(codigo))
                .findFirst()
                .orElseThrow(() -> new DomainException("Estatus inválido: " + codigo));
    }
}