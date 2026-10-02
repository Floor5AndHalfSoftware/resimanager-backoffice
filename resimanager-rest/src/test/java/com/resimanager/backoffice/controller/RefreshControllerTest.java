package com.resimanager.backoffice.controller;

import com.resimanager.backoffice.domain.model.Persona;
import com.resimanager.backoffice.domain.model.ResultadoRotacionRefresh;
import com.resimanager.backoffice.domain.port.in.RefreshTokenUseCase;
import com.resimanager.backoffice.domain.port.in.UsuarioUseCase;
import com.resimanager.backoffice.dto.UserInfoDTO;
import com.resimanager.backoffice.service.ContextoService;
import com.resimanager.backoffice.service.JwtService;
import com.resimanager.backoffice.service.mapper.PersonaMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RefreshControllerTest {

    private RefreshTokenUseCase refreshTokenUseCase;
    private UsuarioUseCase usuarioUseCase;
    private ContextoService contextoService;
    private JwtService jwtService;
    private PersonaMapper personaMapper;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        refreshTokenUseCase = mock(RefreshTokenUseCase.class);
        usuarioUseCase = mock(UsuarioUseCase.class);
        contextoService = mock(ContextoService.class);
        jwtService = mock(JwtService.class);
        personaMapper = mock(PersonaMapper.class);

        RefreshController controller = new RefreshController(
                refreshTokenUseCase, usuarioUseCase, contextoService, jwtService, personaMapper);
        ReflectionTestUtils.setField(controller, "cookieSecure", false);
        ReflectionTestUtils.setField(controller, "accessTokenTtlMinutes", 30L);
        ReflectionTestUtils.setField(controller, "refreshTokenTtlSeconds", 604800L);

        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void refreshValidoEmiteAccessTokenYRenuevaCookies() throws Exception {
        when(refreshTokenUseCase.rotar("refresh-viejo", null))
                .thenReturn(Optional.of(new ResultadoRotacionRefresh("refresh-nuevo", 1, "ADMINISTRADORA", 2, 3)));
        Persona persona = new Persona();
        persona.setId(1);
        persona.setPerUsuario("admin");
        when(usuarioUseCase.obtenerUsuario(1)).thenReturn(Optional.of(persona));
        when(personaMapper.toUserInfoDTO(persona)).thenReturn(UserInfoDTO.builder().id(1).usuario("admin").build());
        when(contextoService.getRolesFromProfiles(1)).thenReturn(List.of("ROLE_USER"));
        when(jwtService.generarTokenConContexto(any(), any(), any())).thenReturn("access-nuevo");

        MvcResult result = mvc.perform(post("/v1/refresh").cookie(new Cookie("refresh", "refresh-viejo")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("access-nuevo"))
                .andReturn();

        Cookie[] cookies = result.getResponse().getCookies();
        assertThat(cookies).extracting(Cookie::getName).contains("jwt", "refresh");
    }

    @Test
    void refreshAusenteEsRechazadoCon401() throws Exception {
        when(refreshTokenUseCase.rotar(any(), any())).thenReturn(Optional.empty());

        mvc.perform(post("/v1/refresh"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshInvalidoEsRechazadoCon401() throws Exception {
        when(refreshTokenUseCase.rotar(any(), any())).thenReturn(Optional.empty());

        mvc.perform(post("/v1/refresh").cookie(new Cookie("refresh", "invalido")))
                .andExpect(status().isUnauthorized());
    }
}
