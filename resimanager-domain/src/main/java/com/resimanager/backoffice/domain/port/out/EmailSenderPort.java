package com.resimanager.backoffice.domain.port.out;

/**
 * Puerto de salida de envío de emails.
 */
public interface EmailSenderPort {

    void enviar(String destinatario, String asunto, String cuerpo);
}
