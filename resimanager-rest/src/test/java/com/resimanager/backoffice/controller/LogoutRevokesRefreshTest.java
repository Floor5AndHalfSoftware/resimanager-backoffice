package com.resimanager.backoffice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.resimanager.backoffice.domain.model.ContextoRefresh;
import com.resimanager.backoffice.domain.model.RefreshToken;
import com.resimanager.backoffice.domain.model.ResultadoRotacionRefresh;
import com.resimanager.backoffice.domain.port.in.UsuarioUseCase;
import com.resimanager.backoffice.domain.port.out.RefreshTokenRepositoryPort;
import com.resimanager.backoffice.service.ContextoService;
import com.resimanager.backoffice.service.JwtService;
import com.resimanager.backoffice.service.RefreshTokenGenerator;
import com.resimanager.backoffice.service.RefreshTokenService;
import com.resimanager.backoffice.service.UserService;
import com.resimanager.backoffice.service.mapper.PersonaMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integra el logout con la revocación real del refresh y comprueba que un
 * refresh token usado tras el logout ya no puede renovar la sesión.
 */
class LogoutRevokesRefreshTest {

    private final RefreshTokenGenerator generator = new RefreshTokenGenerator();
    private final InMemoryRefreshTokenRepository repository = new InMemoryRefreshTokenRepository();
    private RefreshTokenService refreshTokenService;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        refreshTokenService = new RefreshTokenService(repository, generator);
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenTtlSeconds", 604800L);

        LoginController loginController = new LoginController(
                mock(AuthenticationManager.class),
                mock(JwtService.class),
                new ObjectMapper(),
                mock(ContextoService.class),
                mock(UserService.class),
                mock(PersonaMapper.class),
                refreshTokenService);
        ReflectionTestUtils.setField(loginController, "cookieSecure", false);
        ReflectionTestUtils.setField(loginController, "accessTokenTtlMinutes", 30L);
        ReflectionTestUtils.setField(loginController, "refreshTokenTtlSeconds", 604800L);

        RefreshController refreshController = new RefreshController(
                refreshTokenService,
                mock(UsuarioUseCase.class),
                mock(ContextoService.class),
                mock(JwtService.class),
                mock(PersonaMapper.class));
        ReflectionTestUtils.setField(refreshController, "cookieSecure", false);
        ReflectionTestUtils.setField(refreshController, "accessTokenTtlMinutes", 30L);
        ReflectionTestUtils.setField(refreshController, "refreshTokenTtlSeconds", 604800L);

        mvc = MockMvcBuilders.standaloneSetup(loginController, refreshController).build();
    }

    @Test
    void refreshTrasLogoutResponde401() throws Exception {
        ResultadoRotacionRefresh emitido = refreshTokenService.emitirNuevo(1, ContextoRefresh.ninguno());
        String token = emitido.refreshToken();

        mvc.perform(post("/v1/logout").cookie(new Cookie("refresh", token)))
                .andExpect(status().isOk());

        mvc.perform(post("/v1/refresh").cookie(new Cookie("refresh", token)))
                .andExpect(status().isUnauthorized());
    }

    static class InMemoryRefreshTokenRepository implements RefreshTokenRepositoryPort {
        private final Map<String, RefreshToken> porHash = new LinkedHashMap<>();
        private int secuencia = 0;

        @Override
        public RefreshToken guardar(RefreshToken token) {
            if (token.getId() == null) {
                token.setId(++secuencia);
            }
            porHash.put(token.getTokenHash(), token);
            return token;
        }

        @Override
        public Optional<RefreshToken> buscarPorHash(String tokenHash) {
            return Optional.ofNullable(porHash.get(tokenHash));
        }

        @Override
        public int revocarFamilia(UUID familyId) {
            int afectados = 0;
            for (RefreshToken token : porHash.values()) {
                if (familyId.equals(token.getFamilyId()) && !"S".equals(token.getRevocado())) {
                    token.setRevocado("S");
                    afectados++;
                }
            }
            return afectados;
        }

        @Override
        public void revocarPorId(Integer id) {
            porHash.values().stream()
                    .filter(token -> id.equals(token.getId()))
                    .forEach(token -> token.setRevocado("S"));
        }

        @Override
        public int revocarTodasPorPersona(Integer personaId) {
            int afectados = 0;
            for (RefreshToken token : porHash.values()) {
                if (personaId.equals(token.getPersonaId()) && !"S".equals(token.getRevocado())) {
                    token.setRevocado("S");
                    afectados++;
                }
            }
            return afectados;
        }

        @Override
        public int eliminarExpirados(OffsetDateTime momento) {
            int antes = porHash.size();
            porHash.entrySet().removeIf(e -> e.getValue().getExpira().isBefore(momento));
            return antes - porHash.size();
        }
    }
}
