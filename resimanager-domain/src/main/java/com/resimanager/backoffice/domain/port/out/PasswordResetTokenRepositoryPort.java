package com.resimanager.backoffice.domain.port.out;

import com.resimanager.backoffice.domain.model.PasswordResetToken;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * Puerto de salida de persistencia de tokens de restablecimiento de contraseña.
 */
public interface PasswordResetTokenRepositoryPort {

    PasswordResetToken guardar(PasswordResetToken token);

    Optional<PasswordResetToken> buscarPorHash(String tokenHash);

    /** Marca como usados todos los tokens pendientes de una persona. Devuelve cuántos afectó. */
    int invalidarPorPersona(Integer personaId);

    /** Elimina los tokens ya expirados. Devuelve cuántos eliminó. */
    int eliminarExpirados(OffsetDateTime momento);
}
