package com.resimanager.backoffice.domain.port.in;

/**
 * Casos de uso de recuperación de contraseña.
 */
public interface PasswordRecoveryUseCase {

    /**
     * Solicita el restablecimiento para un email. No revela si la cuenta existe:
     * siempre termina sin error salvo que se exceda el límite de solicitudes.
     */
    void solicitar(String email);

    /**
     * Restablece la contraseña con un token válido, la actualiza y cierra las
     * sesiones activas del usuario.
     */
    void restablecer(String token, String password);
}
