package com.resimanager.backoffice.domain.model;

/**
 * Contexto activo asociado a un refresh token. Todos los campos son opcionales
 * porque en el login aún no se ha elegido contexto (multi-tenant).
 */
public record ContextoRefresh(
        String tipo,
        Integer entidadId,
        Integer perfilId
) {

    public static ContextoRefresh ninguno() {
        return new ContextoRefresh(null, null, null);
    }

    public boolean presente() {
        return tipo != null && entidadId != null && perfilId != null;
    }
}
