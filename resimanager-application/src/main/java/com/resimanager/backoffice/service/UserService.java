package com.resimanager.backoffice.service;

import com.resimanager.backoffice.domain.model.AuthUser;
import com.resimanager.backoffice.domain.model.Persona;
import com.resimanager.backoffice.domain.port.in.AuthUseCase;
import com.resimanager.backoffice.domain.port.out.PersonaRepositoryPort;
import com.resimanager.backoffice.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements AuthUseCase {

    private final PersonaRepositoryPort personaRepositoryPort;
    private final ContextoService contextoService;

    @Override
    public AuthUser cargarUsuarioAutenticable(String usuarioOEmail) {
        try {
            Persona persona = personaRepositoryPort.buscarPorUsuarioOEmail(usuarioOEmail, usuarioOEmail)
                    .orElseThrow(() -> new ServiceException("User not found: " + usuarioOEmail, 404));

            if (!"A".equals(persona.getPerSts())) {
                throw new ServiceException("User account is inactive", 403);
            }

            List<String> roles = contextoService.getRolesFromProfiles(persona.getId());
            Set<String> authorities = new HashSet<>(roles);

            return new AuthUser(persona.getId(), persona.getPerUsuario(), persona.getPerClave(), authorities);

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