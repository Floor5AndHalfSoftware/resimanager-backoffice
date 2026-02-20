package com.resimanager.backoffice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resimanager.backoffice.dto.*;
import com.resimanager.backoffice.persistance.repository.PersonaRepository;
import com.resimanager.backoffice.service.ContextoService;
import com.resimanager.backoffice.service.JwtService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.resimanager.backoffice.utils.Constants.API_VERSION_PATH;

@RestController
@RequestMapping(value = API_VERSION_PATH)
@Validated
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final ContextoService contextoService;
    private final PersonaRepository personaRepository;

    @PostMapping(value = "/login", produces = "application/json", consumes = "application/json")
    public ResponseEntity<LoginResponseJson> login(@RequestBody @NotNull LoginRequestJson loginRequestJson) {
        log.info("Login attempt for user: {}", loginRequestJson.getUsername());
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestJson.getUsername(), loginRequestJson.getPassword())
        );
        
        if (authentication.isAuthenticated()) {
            // Get user data from database
            var persona = personaRepository.findByPerUsuarioOrPerEMail(loginRequestJson.getUsername(), loginRequestJson.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found after authentication"));
            
            // Build UserInfoDTO for JWT claims
            UserInfoDTO userInfo = UserInfoDTO.builder()
                    .id(persona.getId())
                    .usuario(persona.getPerUsuario())
                    .nombre(persona.getPerNombre())
                    .apellido(persona.getPerApellido())
                    .email(persona.getPerEMail())
                    .documento(persona.getPerDocIdent())
                    .build();
            
            // Generate JWT token with user info in claims
            var token = jwtService.generateTokenWithUserInfo(authentication, userInfo);
            
            // Get available contexts for the user
            List<ContextoDTO> contextos = contextoService.getContextosDisponibles(persona.getId());
            
            log.info("Login successful for user: {} (ID: {}) with {} contexts", 
                    persona.getPerUsuario(), persona.getId(), contextos.size());
            
            return ResponseEntity.ok().body(LoginResponseJson.builder()
                    .token(token)
                    .type("Bearer")
                    .usuario(userInfo)
                    .contextosDisponibles(contextos)
                    .build());
        } else {
            throw new UsernameNotFoundException("invalid user request");
        }
    }
}

