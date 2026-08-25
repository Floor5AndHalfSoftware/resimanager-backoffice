package com.resimanager.backoffice.service;

import com.resimanager.backoffice.exception.ServiceException;
import com.resimanager.backoffice.domain.model.Persona;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.resimanager.backoffice.dto.AuthDto;
import com.resimanager.backoffice.persistance.repository.PersonaRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private final PersonaRepository personaRepository;
    private final ContextoService contextoService;

    /**
     * Load user by username or email for authentication
     * @param username Username or email to search
     * @return AuthDto with user credentials and authorities
     */
    public AuthDto loadUserByUsername(String username) {
        log.debug("Loading user by username: {}", username);
        
        try {
            // Search by username OR email (allows login with either)
            var persona = personaRepository.findByPerUsuarioOrPerEMail(username, username)
                    .orElseThrow(() -> new ServiceException("User not found: " + username, 404));
            
            // Check if user is active
            if (!"A".equals(persona.getPerSts())) {
                log.warn("Inactive user attempted login: {}", username);
                throw new ServiceException("User account is inactive", 403);
            }
            
            // Get roles from user's profiles in database
            List<String> roles = contextoService.getRolesFromProfiles(persona.getId());
            Set<String> authorities = new HashSet<>(roles);
            
            log.info("User loaded successfully: {} (ID: {}) with {} roles", 
                    persona.getPerUsuario(), persona.getId(), authorities.size());
            log.debug("User {} authorities: {}", persona.getPerUsuario(), authorities);
            
            return AuthDto.builder()
                    .userId(persona.getId())
                    .username(persona.getPerUsuario())
                    .password(persona.getPerClave())  // SHA-256 hash from database
                    .authorities(authorities)
                    .build();
                    
        } catch (ServiceException e) {
            log.error("Error loading user: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error loading user: {}", e.getMessage(), e);
            throw new ServiceException("Error loading user: " + e.getMessage(), 500);
        }
    }
    
    /**
     * Get user entity by username
     * @param username Username to search
     * @return Optional containing the Persona entity if found
     */
    public Optional<Persona> getUserByUsername(String username) {
        log.debug("Getting user entity by username: {}", username);
        return personaRepository.findByPerUsuarioOrPerEMail(username, username);
    }

}
