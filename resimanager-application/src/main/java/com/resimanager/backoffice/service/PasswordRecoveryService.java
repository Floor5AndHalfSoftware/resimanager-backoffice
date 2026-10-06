package com.resimanager.backoffice.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.resimanager.backoffice.domain.model.PasswordResetToken;
import com.resimanager.backoffice.domain.model.Persona;
import com.resimanager.backoffice.domain.port.in.PasswordRecoveryUseCase;
import com.resimanager.backoffice.domain.port.out.EmailSenderPort;
import com.resimanager.backoffice.domain.port.out.PasswordEncoderPort;
import com.resimanager.backoffice.domain.port.out.PasswordResetTokenRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PersonaRepositoryPort;
import com.resimanager.backoffice.domain.port.out.RefreshTokenRepositoryPort;
import com.resimanager.backoffice.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Recuperación de contraseña: solicitud por email (anti-enumeración) y
 * restablecimiento con token de un solo uso, cerrando las sesiones activas.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordRecoveryService implements PasswordRecoveryUseCase {

    private static final String EST_ISSUE = "PWD-RESET-ISSUE";
    private static final String EST_CONSUME = "PWD-RESET-CONSUME";
    private static final int MAX_SOLICITUDES = 5;

    private final PersonaRepositoryPort personaRepositoryPort;
    private final PasswordResetTokenRepositoryPort passwordResetTokenRepositoryPort;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final EmailSenderPort emailSenderPort;
    private final RefreshTokenGenerator refreshTokenGenerator;

    /** Rate limiting de solicitudes: máximo de intentos por email en la ventana. */
    private final Cache<String, Integer> solicitudes = Caffeine.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();

    @Value("${app.security.password-reset-ttl-minutes:30}")
    private long ttlMinutes;

    @Value("${app.security.password-min-length:8}")
    private int minLength;

    @Value("${app.frontend.base-url:http://localhost:5000}")
    private String frontendBaseUrl;

    @Value("${app.mail.enabled:true}")
    private boolean mailEnabled;

    @Override
    @Transactional
    public void solicitar(String email) {
        if (email == null || email.isBlank()) {
            return;
        }
        String emailLimpio = email.trim();
        registrarIntento(emailLimpio.toLowerCase());

        Optional<Persona> personaOpt = personaRepositoryPort
                .buscarPorUsuarioOEmail(emailLimpio, emailLimpio)
                .filter(p -> "A".equals(p.getPerSts()));

        if (personaOpt.isEmpty()) {
            // Anti-enumeración: misma respuesta que cuando la cuenta existe.
            log.debug("Solicitud de restablecimiento para email no encontrado o inactivo");
            return;
        }

        Persona persona = personaOpt.get();
        passwordResetTokenRepositoryPort.invalidarPorPersona(persona.getId());

        String token = refreshTokenGenerator.generarToken();
        OffsetDateTime ahora = OffsetDateTime.now();
        PasswordResetToken registro = new PasswordResetToken();
        registro.setTokenHash(refreshTokenGenerator.hash(token));
        registro.setPersonaId(persona.getId());
        registro.setExpira(ahora.plusMinutes(ttlMinutes));
        registro.setUsrCrea(persona.getId());
        registro.setFchHorCrea(ahora);
        registro.setEstCrea(EST_ISSUE);
        registro.setUsrMod(persona.getId());
        registro.setFchHorMod(ahora);
        registro.setEstMod(EST_ISSUE);
        passwordResetTokenRepositoryPort.guardar(registro);

        enviarCorreo(persona, token);
    }

    @Override
    @Transactional
    public void restablecer(String token, String password) {
        if (password == null || password.length() < minLength) {
            throw new ServiceException(
                    "La contraseña no cumple la política mínima (" + minLength + " caracteres).", 400);
        }
        if (token == null || token.isBlank()) {
            throw new ServiceException("Token inválido o expirado.", 400);
        }

        PasswordResetToken registro = passwordResetTokenRepositoryPort
                .buscarPorHash(refreshTokenGenerator.hash(token))
                .orElseThrow(() -> new ServiceException("Token inválido o expirado.", 400));

        OffsetDateTime ahora = OffsetDateTime.now();
        if (registro.estaUsado() || registro.estaExpirado(ahora)) {
            throw new ServiceException("Token inválido o expirado.", 400);
        }

        Persona persona = personaRepositoryPort.buscarPorId(registro.getPersonaId())
                .orElseThrow(() -> new ServiceException("Token inválido o expirado.", 400));

        persona.setPerClave(passwordEncoderPort.codificar(password));
        persona.setPerFchHorMod(ahora);
        personaRepositoryPort.guardar(persona);

        registro.setUsado(ahora);
        registro.setEstMod(EST_CONSUME);
        registro.setFchHorMod(ahora);
        passwordResetTokenRepositoryPort.guardar(registro);
        passwordResetTokenRepositoryPort.invalidarPorPersona(persona.getId());

        refreshTokenRepositoryPort.revocarTodasPorPersona(persona.getId());
        log.info("Contraseña restablecida para persona {}; sesiones cerradas", persona.getId());
    }

    private void registrarIntento(String clave) {
        Integer actual = solicitudes.getIfPresent(clave);
        int intentos = actual == null ? 0 : actual;
        if (intentos >= MAX_SOLICITUDES) {
            throw new ServiceException("Demasiadas solicitudes de restablecimiento. Intente más tarde.", 429);
        }
        solicitudes.put(clave, intentos + 1);
    }

    private void enviarCorreo(Persona persona, String token) {
        if (!mailEnabled) {
            log.warn("Envío de email deshabilitado (app.mail.enabled=false); no se envía el enlace");
            return;
        }
        String enlace = frontendBaseUrl + "/reset-password?token=" + token;
        String asunto = "Restablecimiento de contraseña - ResiManager";
        String cuerpo = "Hola " + persona.getPerNombre() + ",\n\n"
                + "Recibimos una solicitud para restablecer tu contraseña. "
                + "Usa el siguiente enlace (válido por " + ttlMinutes + " minutos):\n\n"
                + enlace + "\n\n"
                + "Si no solicitaste este cambio, ignora este mensaje.";
        try {
            emailSenderPort.enviar(persona.getPerEMail(), asunto, cuerpo);
        } catch (Exception e) {
            // Fail-soft: no se filtra el resultado al cliente.
            log.error("No se pudo enviar el email de restablecimiento: {}", e.getMessage());
        }
    }
}
