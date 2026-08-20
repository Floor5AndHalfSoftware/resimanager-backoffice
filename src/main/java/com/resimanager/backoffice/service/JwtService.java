package com.resimanager.backoffice.service;

import com.resimanager.backoffice.dto.ContextoActualDTO;
import com.resimanager.backoffice.dto.UserInfoDTO;
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
    
    public String generateTokenWithUserInfo(Authentication auth, UserInfoDTO userInfo) {
        var cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + TOKEN_EXPIRATION_TIME_IN_MINUTES);

        return buildTokenWithUserInfo(auth, userInfo, null, cal);
    }
    
    public String generateTokenWithContext(Authentication auth, UserInfoDTO userInfo, ContextoActualDTO contexto) {
        var cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + TOKEN_EXPIRATION_TIME_IN_MINUTES);

        return buildTokenWithUserInfo(auth, userInfo, contexto, cal);
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
    
    private static String buildTokenWithUserInfo(Authentication auth, UserInfoDTO userInfo, ContextoActualDTO contexto, Calendar cal) {
        SecretKey key = getSigningKey();
        var preToken = Jwts.builder()
                .issuedAt(new Date())
                .issuer(ISSUER_INFO)
                .subject(auth.getName())
                .expiration(cal.getTime())
                .signWith(key);
        
        // Add roles
        preToken.claim("roles", auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        
        // Add user info
        if (userInfo != null) {
            preToken.claim("userId", userInfo.id());
            preToken.claim("nombre", userInfo.nombre());
            preToken.claim("apellido", userInfo.apellido());
            preToken.claim("email", userInfo.email());
            preToken.claim("documento", userInfo.documento());
        }
        
        // Add context info
        if (contexto != null) {
            preToken.claim("contextoTipo", contexto.tipo());
            preToken.claim("contextoEntidadId", contexto.entidadId());
            preToken.claim("contextoEntidadNombre", contexto.entidadNombre());
            preToken.claim("contextoPerfilId", contexto.perfilId());
            preToken.claim("contextoPerfilNombre", contexto.perfilNombre());
        }
        
        return preToken.compact();
    }
}
