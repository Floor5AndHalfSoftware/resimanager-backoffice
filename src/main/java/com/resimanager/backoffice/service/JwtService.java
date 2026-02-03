package com.resimanager.backoffice.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;

import static com.resimanager.backoffice.utils.Constants.ISSUER_INFO;
import static com.resimanager.backoffice.utils.Constants.SUPER_SECRET_KEY;
import static com.resimanager.backoffice.utils.Constants.TOKEN_EXPIRATION_TIME_IN_MINUTES;

@Component
public class JwtService {

    private static SecretKey getSigningKey() {
        try {
            // Generar una clave de 64 bytes usando SHA-512
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hash = digest.digest(SUPER_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error generating signing key", e);
        }
    }

    public String generateToken(Authentication auth) {
        var cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + TOKEN_EXPIRATION_TIME_IN_MINUTES);

        return buildToken(auth, cal);
    }

    private static String buildToken(Authentication auth, Calendar cal) {
        SecretKey key = getSigningKey();
        var preToken = Jwts.builder()
                .issuedAt(new Date())
                .issuer(ISSUER_INFO)
                .subject(auth.getName())
                .expiration(cal.getTime())
                .signWith(key);
        preToken.claim("roles", auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return preToken.compact();
    }
}