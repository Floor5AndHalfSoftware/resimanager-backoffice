package com.resimanager.backoffice.config.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.resimanager.backoffice.controller.handler.json.HttpErrorInfoJson;
import com.resimanager.backoffice.exception.ServiceException;
import com.resimanager.backoffice.utils.FormatUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import javax.crypto.SecretKey;

import static com.resimanager.backoffice.utils.Constants.HEADER_AUTHORIZACION_KEY;
import static com.resimanager.backoffice.utils.Constants.SUPER_SECRET_KEY;
import static com.resimanager.backoffice.utils.Constants.TOKEN_BEARER_PREFIX;

@Slf4j
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    public JWTAuthorizationFilter(AuthenticationManager authManager) {
        super(authManager);
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

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        log.debug("JWT Filter - Request URI: {} {}", req.getMethod(), req.getRequestURI());
        
        final String header = req.getHeader(HEADER_AUTHORIZACION_KEY);
        if (header == null || !header.startsWith(TOKEN_BEARER_PREFIX)) {
            log.debug("JWT Filter - No token or invalid prefix, continuing chain");
            chain.doFilter(req, res);
            return;
        }

        final UsernamePasswordAuthenticationToken authentication;
        try {
            authentication = getAuthentication(req);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("JWT Filter - Authentication successful for user: {}", authentication.getName());
            chain.doFilter(req, res);
        } catch (ServiceException ex) {
            log.error("JWT Filter - Authentication failed: {}", ex.getMessage());
            final ObjectMapper mapper = new ObjectMapper();
            final HttpErrorInfoJson httpErrorInfoDto = FormatUtils.httpErrorInfoFormatted(HttpStatus.UNAUTHORIZED, req, ex);

            res.setContentType("application/json;charset=UTF-8");
            res.setStatus(HttpStatus.UNAUTHORIZED.value());
            res.getWriter().write(mapper.writeValueAsString(httpErrorInfoDto));
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_AUTHORIZACION_KEY);
        if (token != null) {
            // Se procesa el token y se recupera el usuario.
            token = token.replace(TOKEN_BEARER_PREFIX, "");
            try {
                SecretKey key = getSigningKey();
                String user = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .getSubject();
                if (user != null) {
                    return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                }
            } catch (Exception exception) {
                throw new ServiceException("Authentication was not posible: " + exception.getMessage(), 403);
            }

            return null;
        }
        return null;
    }
}