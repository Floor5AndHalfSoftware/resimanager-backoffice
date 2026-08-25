package com.resimanager.backoffice.config.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.resimanager.backoffice.dto.HttpErrorInfoJson;
import com.resimanager.backoffice.exception.ServiceException;
import com.resimanager.backoffice.controller.handler.FormatUtils;
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
        
        // Try to get token from Authorization header first (backward compatibility)
        String header = req.getHeader(HEADER_AUTHORIZACION_KEY);
        String token = null;
        
        if (header != null && header.startsWith(TOKEN_BEARER_PREFIX)) {
            token = header;
            log.debug("JWT Filter - Token found in Authorization header");
        } else {
            // If not in header, try to get from cookie
            Cookie[] cookies = req.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("jwt".equals(cookie.getName())) {
                        token = TOKEN_BEARER_PREFIX + cookie.getValue();
                        log.debug("JWT Filter - Token found in cookie");
                        break;
                    }
                }
            }
        }
        
        if (token == null) {
            log.debug("JWT Filter - No token found in header or cookie, continuing chain");
            chain.doFilter(req, res);
            return;
        }

        final UsernamePasswordAuthenticationToken authentication;
        try {
            authentication = getAuthentication(token);
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

    private UsernamePasswordAuthenticationToken getAuthentication(String tokenWithPrefix) {
        if (tokenWithPrefix != null) {
            // Remove Bearer prefix
            String token = tokenWithPrefix.replace(TOKEN_BEARER_PREFIX, "");
            try {
                SecretKey key = getSigningKey();
                String user = Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .getSubject();
                if (user != null) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                    auth.setDetails(token);
                    return auth;
                }
            } catch (Exception exception) {
                throw new ServiceException("Authentication was not possible: " + exception.getMessage(), 403);
            }

            return null;
        }
        return null;
    }
}