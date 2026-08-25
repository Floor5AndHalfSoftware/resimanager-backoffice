package com.resimanager.backoffice.service;

import com.resimanager.backoffice.domain.model.Persona;
import com.resimanager.backoffice.domain.port.out.PersonaRepositoryPort;
import com.resimanager.backoffice.exception.ServiceException;
import com.resimanager.backoffice.dto.AuthDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private final PersonaRepositoryPort personaRepositoryPort;
    private final ContextoService contextoService;

    public AuthDto loadUserByUsername(String username) {
        try {
            var persona = personaRepositoryPort.buscarPorUsuarioOEmail(username, username)
                    .orElseThrow(() -> new ServiceException("User not found: " + username, 404));

            if (!"A".equals(persona.getPerSts())) {
                throw new ServiceException("User account is inactive", 403);
            }

            List<String> roles = contextoService.getRolesFromProfiles(persona.getId());
            Set<String> authorities = new HashSet<>(roles);

            return AuthDto.builder()
                    .userId(persona.getId())
                    .username(persona.getPerUsuario())
                    .password(persona.getPerClave())
                    .authorities(authorities)
                    .build();

        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error loading user: {}", e.getMessage(), e);
            throw new ServiceException("Error loading user: " + e.getMessage(), 500);
        }
    }

    public Optional<Persona> getUserByUsername(String username) {
        return personaRepositoryPort.buscarPorUsuarioOEmail(username, username);
    }
}