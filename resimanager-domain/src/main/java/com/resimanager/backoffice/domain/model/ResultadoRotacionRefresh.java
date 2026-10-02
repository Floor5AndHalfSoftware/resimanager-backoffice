package com.resimanager.backoffice.domain.model;

/**
 * Resultado de rotar (o emitir) un refresh token: el nuevo token en claro
 * (que solo se entrega una vez al cliente) junto con el usuario y contexto
 * almacenados, necesarios para reconstruir el access token.
 */
public record ResultadoRotacionRefresh(
        String refreshToken,
        Integer personaId,
        String contextoTipo,
        Integer contextoEntidadId,
        Integer contextoPerfilId
) {

    public boolean tieneContexto() {
        return contextoTipo != null && contextoEntidadId != null && contextoPerfilId != null;
    }
}
