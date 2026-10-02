package com.resimanager.backoffice.service;

import com.resimanager.backoffice.domain.port.out.JwtPort;
import com.resimanager.backoffice.dto.ContextoActualDTO;
import com.resimanager.backoffice.dto.UserInfoDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

import static com.resimanager.backoffice.utils.Constants.ISSUER_INFO;
import static com.resimanager.backoffice.utils.Constants.SUPER_SECRET_KEY;

@Component
public class JwtService implements JwtPort {

    private final long accessTokenTtlMinutes;

    public JwtService(@Value("${app.security.access-token-ttl-minutes:30}") long accessTokenTtlMinutes) {
        this.accessTokenTtlMinutes = accessTokenTtlMinutes;
    }

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

    private Calendar expiracionAccessToken() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + (int) accessTokenTtlMinutes);
        return cal;
    }

    @Override
    public String generarToken(String username, Integer userId, String nombre, String apellido,
                               String email, String documento, Set<String> roles) {
        Calendar cal = expiracionAccessToken();

        SecretKey key = getSigningKey();
        var preToken = Jwts.builder()
                .issuedAt(new Date())
                .issuer(ISSUER_INFO)
                .subject(username)
                .expiration(cal.getTime())
                .signWith(key);
        preToken.claim("tokenType", "access");
        preToken.claim("roles", roles);
        if (userId != null) preToken.claim("userId", userId);
        if (nombre != null) preToken.claim("nombre", nombre);
        if (apellido != null) preToken.claim("apellido", apellido);
        if (email != null) preToken.claim("email", email);
        if (documento != null) preToken.claim("documento", documento);
        return preToken.compact();
    }

    @Override
    public boolean esTokenValido(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String obtenerUsuarioDelToken(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    public String generateToken(Authentication auth) {
        return buildToken(auth, expiracionAccessToken());
    }

    public String generateTokenWithUserInfo(Authentication auth, UserInfoDTO userInfo) {
        return buildTokenWithUserInfo(userInfo, null, authorities(auth), expiracionAccessToken());
    }

    public String generateTokenWithContext(Authentication auth, UserInfoDTO userInfo, ContextoActualDTO contexto) {
        return buildTokenWithUserInfo(userInfo, contexto, authorities(auth), expiracionAccessToken());
    }

    /**
     * Genera un access token a partir de datos ya resueltos (sin {@link Authentication}),
     * usado al renovar la sesión con un refresh token.
     */
    public String generarTokenConContexto(UserInfoDTO userInfo, ContextoActualDTO contexto, Collection<String> roles) {
        return buildTokenWithUserInfo(userInfo, contexto, roles, expiracionAccessToken());
    }

    private static Collection<String> authorities(Authentication auth) {
        return auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
    }

    private static String buildToken(Authentication auth, Calendar cal) {
        SecretKey key = getSigningKey();
        var preToken = Jwts.builder()
                .issuedAt(new Date())
                .issuer(ISSUER_INFO)
                .subject(auth.getName())
                .expiration(cal.getTime())
                .signWith(key);
        preToken.claim("tokenType", "access");
        preToken.claim("roles", auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return preToken.compact();
    }

    private static String buildTokenWithUserInfo(UserInfoDTO userInfo, ContextoActualDTO contexto,
                                                 Collection<String> roles, Calendar cal) {
        SecretKey key = getSigningKey();
        var preToken = Jwts.builder()
                .issuedAt(new Date())
                .issuer(ISSUER_INFO)
                .subject(userInfo.usuario())
                .expiration(cal.getTime())
                .signWith(key);

        preToken.claim("tokenType", "access");
        preToken.claim("roles", roles);

        // Add user info
        if (userInfo != null) {
            if (userInfo.id() != null) preToken.claim("userId", userInfo.id());
            if (userInfo.nombre() != null) preToken.claim("nombre", userInfo.nombre());
            if (userInfo.apellido() != null) preToken.claim("apellido", userInfo.apellido());
            if (userInfo.email() != null) preToken.claim("email", userInfo.email());
            if (userInfo.documento() != null) preToken.claim("documento", userInfo.documento());
        }

        // Add context info
        if (contexto != null) {
            if (contexto.tipo() != null) preToken.claim("contextoTipo", contexto.tipo());
            if (contexto.entidadId() != null) preToken.claim("contextoEntidadId", contexto.entidadId());
            if (contexto.entidadNombre() != null) preToken.claim("contextoEntidadNombre", contexto.entidadNombre());
            if (contexto.perfilId() != null) preToken.claim("contextoPerfilId", contexto.perfilId());
            if (contexto.perfilNombre() != null) preToken.claim("contextoPerfilNombre", contexto.perfilNombre());
        }

        return preToken.compact();
    }
}
