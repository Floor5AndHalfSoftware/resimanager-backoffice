package com.resimanager.backoffice.service;

import com.resimanager.backoffice.persistance.repository.AccOpcPerfilRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import static com.resimanager.backoffice.utils.Constants.SUPER_SECRET_KEY;

/**
 * Service for checking user permissions based on their active profile
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionService {

    private final AccOpcPerfilRepository accOpcPerfilRepository;

    /**
     * Check if the current user has permission to access a module with a specific action
     * @param modulo Module name (e.g., "PROPIEDADES", "USUARIOS")
     * @param accion Action name (e.g., "VIS", "INS", "MOD", "EL", "MNJ")
     * @return true if user has permission, false otherwise
     */
    public boolean hasPermission(String modulo, String accion) {
        try {
            // Get current authentication
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                log.warn("No authenticated user found");
                return false;
            }

            // Get JWT token from authentication
            String token = getTokenFromAuthentication(auth);
            if (token == null) {
                log.warn("No JWT token found in authentication");
                return false;
            }

            // Extract profile ID from JWT claims
            Integer perfilId = getActivePerfilFromToken(token);
            if (perfilId == null) {
                log.warn("No active profile found in JWT token for user: {}", auth.getName());
                return false;
            }

            // Check if super admin (profile ID 1 has all permissions)
            if (perfilId == 1) {
                log.debug("Super admin access granted for user: {}", auth.getName());
                return true;
            }

            // Check permission in database
            boolean hasPermission = accOpcPerfilRepository.hasPermission(perfilId, modulo, accion);
            
            log.debug("Permission check for user {}, profile {}, module {}, action {}: {}", 
                    auth.getName(), perfilId, modulo, accion, hasPermission);
            
            return hasPermission;

        } catch (Exception e) {
            log.error("Error checking permissions for module {} and action {}: {}", 
                    modulo, accion, e.getMessage(), e);
            return false;
        }
    }

    /**
     * Extract active profile ID from JWT token
     */
    private Integer getActivePerfilFromToken(String token) {
        try {
            SecretKey key = getSigningKey();
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Get context profile ID from claims
            Object perfilIdObj = claims.get("contextoPerfilId");
            if (perfilIdObj != null) {
                return (Integer) perfilIdObj;
            }

            // If no context is set, return null (user must select a context first)
            return null;

        } catch (Exception e) {
            log.error("Error extracting profile from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extract token from authentication object
     * This is a simplified version - in production, the token would be passed differently
     */
    private String getTokenFromAuthentication(Authentication auth) {
        if (auth.getDetails() instanceof String token) {
            return token;
        }
        return null;
    }

    /**
     * Get signing key for JWT verification
     */
    private SecretKey getSigningKey() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hash = digest.digest(SUPER_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(hash);
        } catch (Exception e) {
            throw new RuntimeException("Error generating signing key", e);
        }
    }
}
