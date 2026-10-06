package com.resimanager.backoffice.infrastructure.adapter;

import com.resimanager.backoffice.domain.port.out.EmailSenderPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Adaptador de envío de email por SMTP usando {@link JavaMailSender}.
 */
@Component
@Slf4j
public class SmtpEmailSenderAdapter implements EmailSenderPort {

    private final JavaMailSender mailSender;
    private final String from;

    public SmtpEmailSenderAdapter(JavaMailSender mailSender,
                                  @Value("${app.mail.from:${spring.mail.username:}}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    public void enviar(String destinatario, String asunto, String cuerpo) {
        SimpleMailMessage message = new SimpleMailMessage();
        if (from != null && !from.isBlank()) {
            message.setFrom(from);
        }
        message.setTo(destinatario);
        message.setSubject(asunto);
        message.setText(cuerpo);
        mailSender.send(message);
        log.debug("Email enviado a {}", destinatario);
    }
}
