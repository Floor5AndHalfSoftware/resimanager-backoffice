package com.resimanager.backoffice.infrastructure.adapter;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SmtpEmailSenderAdapterTest {

    @Test
    void construyeYEnviaElMensaje() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        SmtpEmailSenderAdapter adapter = new SmtpEmailSenderAdapter(mailSender, "no-reply@resimanager.com");

        adapter.enviar("user@x.com", "Restablecimiento",
                "Enlace: http://localhost:5000/reset-password?token=abc123");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage message = captor.getValue();

        assertThat(message.getFrom()).isEqualTo("no-reply@resimanager.com");
        assertThat(message.getTo()).containsExactly("user@x.com");
        assertThat(message.getSubject()).isEqualTo("Restablecimiento");
        assertThat(message.getText()).contains("reset-password?token=abc123");
    }
}
