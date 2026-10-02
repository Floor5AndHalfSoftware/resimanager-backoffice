package com.resimanager.backoffice.service;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;

/**
 * Genera el valor opaco del refresh token y su hash SHA-256.
 * El token en claro se entrega una sola vez al cliente; en base de datos
 * solo se persiste el hash.
 */
@Component
public class RefreshTokenGenerator {

    private static final int TOKEN_BYTES = 32; // 256 bits
    private final SecureRandom secureRandom = new SecureRandom();

    /** Token opaco aleatorio en base64url sin relleno. */
    public String generarToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /** Hash SHA-256 en hexadecimal del token. */
    public String hash(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }
}
