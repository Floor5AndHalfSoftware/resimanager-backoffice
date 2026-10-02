package com.resimanager.backoffice.domain.port.in;

import com.resimanager.backoffice.domain.model.ContextoRefresh;
import com.resimanager.backoffice.domain.model.ResultadoRotacionRefresh;

import java.util.Optional;

/**
 * Casos de uso de gestión del refresh token de sesión.
 */
public interface RefreshTokenUseCase {

    /**
     * Emite un refresh token nuevo en una familia nueva (inicio de sesión o
     * primer contexto). Devuelve el token en claro junto con el contexto.
     */
    ResultadoRotacionRefresh emitirNuevo(Integer personaId, ContextoRefresh contexto);

    /**
     * Rota un refresh token: consume el presentado y emite uno nuevo en la misma
     * familia. Si {@code nuevoContexto} es no nulo, el token nuevo lleva ese
     * contexto; si es nulo, conserva el del token anterior.
     *
     * @return resultado con el nuevo token, o vacío si el token es inválido,
     * expirado o revocado (o si se detectó reutilización).
     */
    Optional<ResultadoRotacionRefresh> rotar(String tokenPlano, ContextoRefresh nuevoContexto);

    /**
     * Revoca la familia del refresh token presentado (cierre de sesión).
     */
    void revocar(String tokenPlano);

    /** Elimina los refresh tokens expirados. Devuelve cuántos eliminó. */
    int eliminarExpirados();
}
