package com.resimanager.backoffice.domain.port.out;

import com.resimanager.backoffice.domain.model.RefreshToken;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida de persistencia de refresh tokens.
 */
public interface RefreshTokenRepositoryPort {

    RefreshToken guardar(RefreshToken token);

    Optional<RefreshToken> buscarPorHash(String tokenHash);

    /** Revoca todos los tokens vigentes de una familia (sesión). Devuelve cuántos afectó. */
    int revocarFamilia(UUID familyId);

    /** Revoca un token concreto por su id. */
    void revocarPorId(Integer id);

    /** Elimina los tokens ya expirados. Devuelve cuántos eliminó. */
    int eliminarExpirados(OffsetDateTime momento);
}
