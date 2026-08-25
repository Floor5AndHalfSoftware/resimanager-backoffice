package com.resimanager.backoffice.domain.exception;

/**
 * Excepción base de dominio para violaciones de reglas de negocio.
 * Toda regla de negocio incumplida se expresa como un {@link DomainException}
 * o una subclase de la misma.
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}