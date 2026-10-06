package com.resimanager.backoffice.service;

import com.resimanager.backoffice.domain.model.PasswordResetToken;
import com.resimanager.backoffice.domain.model.Persona;
import com.resimanager.backoffice.domain.port.out.EmailSenderPort;
import com.resimanager.backoffice.domain.port.out.PasswordEncoderPort;
import com.resimanager.backoffice.domain.port.out.PasswordResetTokenRepositoryPort;
import com.resimanager.backoffice.domain.port.out.PersonaRepositoryPort;
import com.resimanager.backoffice.domain.port.out.RefreshTokenRepositoryPort;
import com.resimanager.backoffice.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PasswordRecoveryServiceTest {

    private final RefreshTokenGenerator generator = new RefreshTokenGenerator();
    private PersonaRepositoryPort personaRepositoryPort;
    private PasswordResetTokenRepositoryPort tokenRepositoryPort;
    private RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private PasswordEncoderPort passwordEncoderPort;
    private EmailSenderPort emailSenderPort;
    private PasswordRecoveryService service;

    @BeforeEach
    void setUp() {
        personaRepositoryPort = mock(PersonaRepositoryPort.class);
        tokenRepositoryPort = mock(PasswordResetTokenRepositoryPort.class);
        refreshTokenRepositoryPort = mock(RefreshTokenRepositoryPort.class);
        passwordEncoderPort = mock(PasswordEncoderPort.class);
        emailSenderPort = mock(EmailSenderPort.class);

        service = new PasswordRecoveryService(personaRepositoryPort, tokenRepositoryPort,
                refreshTokenRepositoryPort, passwordEncoderPort, emailSenderPort, generator);
        ReflectionTestUtils.setField(service, "ttlMinutes", 30L);
        ReflectionTestUtils.setField(service, "minLength", 8);
        ReflectionTestUtils.setField(service, "frontendBaseUrl", "http://localhost:5000");
        ReflectionTestUtils.setField(service, "mailEnabled", true);
    }

    private Persona personaActiva() {
        Persona p = new Persona();
        p.setId(1);
        p.setPerEMail("user@x.com");
        p.setPerNombre("Ana");
        p.setPerSts("A");
        return p;
    }

    private PasswordResetToken token(Integer personaId, OffsetDateTime expira, OffsetDateTime usado) {
        PasswordResetToken t = new PasswordResetToken();
        t.setPersonaId(personaId);
        t.setExpira(expira);
        t.setUsado(usado);
        return t;
    }

    @Test
    void solicitarCuentaActivaGuardaTokenYEnviaEmail() {
        when(personaRepositoryPort.buscarPorUsuarioOEmail("user@x.com", "user@x.com"))
                .thenReturn(Optional.of(personaActiva()));

        service.solicitar("user@x.com");

        verify(tokenRepositoryPort).invalidarPorPersona(1);
        ArgumentCaptor<PasswordResetToken> captor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepositoryPort).guardar(captor.capture());
        PasswordResetToken guardado = captor.getValue();
        assertThat(guardado.getPersonaId()).isEqualTo(1);
        assertThat(guardado.getTokenHash()).hasSize(64);
        assertThat(guardado.estaUsado()).isFalse();
        assertThat(guardado.getExpira()).isAfter(OffsetDateTime.now());

        verify(emailSenderPort).enviar(eq("user@x.com"), anyString(),
                contains("http://localhost:5000/reset-password?token="));
    }

    @Test
    void solicitarEmailInexistenteNoEnviaNiGuarda() {
        when(personaRepositoryPort.buscarPorUsuarioOEmail("no@x.com", "no@x.com"))
                .thenReturn(Optional.empty());

        service.solicitar("no@x.com");

        verifyNoInteractions(tokenRepositoryPort);
        verifyNoInteractions(emailSenderPort);
    }

    @Test
    void solicitarExcedeLimiteLanza429() {
        when(personaRepositoryPort.buscarPorUsuarioOEmail("limite@x.com", "limite@x.com"))
                .thenReturn(Optional.empty());

        for (int i = 0; i < 5; i++) {
            service.solicitar("limite@x.com");
        }

        assertThatThrownBy(() -> service.solicitar("limite@x.com"))
                .isInstanceOf(ServiceException.class)
                .extracting(e -> ((ServiceException) e).getCode())
                .isEqualTo(429);
    }

    @Test
    void restablecerTokenValidoActualizaClaveConsumeYRevoca() {
        PasswordResetToken registro = token(1, OffsetDateTime.now().plusMinutes(30), null);
        when(tokenRepositoryPort.buscarPorHash(generator.hash("tok"))).thenReturn(Optional.of(registro));
        Persona persona = personaActiva();
        when(personaRepositoryPort.buscarPorId(1)).thenReturn(Optional.of(persona));
        when(passwordEncoderPort.codificar("nuevaClave1")).thenReturn("$2a$hash");

        service.restablecer("tok", "nuevaClave1");

        assertThat(persona.getPerClave()).isEqualTo("$2a$hash");
        assertThat(registro.estaUsado()).isTrue();
        verify(tokenRepositoryPort).guardar(registro);
        verify(tokenRepositoryPort).invalidarPorPersona(1);
        verify(refreshTokenRepositoryPort).revocarTodasPorPersona(1);
    }

    @Test
    void restablecerTokenInvalidoLanza400() {
        when(tokenRepositoryPort.buscarPorHash(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.restablecer("malo", "nuevaClave1"))
                .isInstanceOf(ServiceException.class)
                .extracting(e -> ((ServiceException) e).getCode())
                .isEqualTo(400);
    }

    @Test
    void restablecerTokenExpiradoLanza400() {
        PasswordResetToken registro = token(1, OffsetDateTime.now().minusMinutes(1), null);
        when(tokenRepositoryPort.buscarPorHash(generator.hash("tok"))).thenReturn(Optional.of(registro));

        assertThatThrownBy(() -> service.restablecer("tok", "nuevaClave1"))
                .isInstanceOf(ServiceException.class)
                .extracting(e -> ((ServiceException) e).getCode())
                .isEqualTo(400);
        verify(refreshTokenRepositoryPort, never()).revocarTodasPorPersona(any());
    }

    @Test
    void restablecerTokenYaUsadoLanza400() {
        PasswordResetToken registro = token(1, OffsetDateTime.now().plusMinutes(30), OffsetDateTime.now());
        when(tokenRepositoryPort.buscarPorHash(generator.hash("tok"))).thenReturn(Optional.of(registro));

        assertThatThrownBy(() -> service.restablecer("tok", "nuevaClave1"))
                .isInstanceOf(ServiceException.class)
                .extracting(e -> ((ServiceException) e).getCode())
                .isEqualTo(400);
    }

    @Test
    void restablecerPasswordDebilLanza400() {
        assertThatThrownBy(() -> service.restablecer("tok", "corta"))
                .isInstanceOf(ServiceException.class)
                .extracting(e -> ((ServiceException) e).getCode())
                .isEqualTo(400);
        verifyNoInteractions(tokenRepositoryPort);
    }
}
