package com.resimanager.backoffice.config.security.jwt;

import com.resimanager.backoffice.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class JWTAuthorizationFilterTest {

    private final JWTAuthorizationFilter filter =
            new JWTAuthorizationFilter(mock(AuthenticationManager.class));
    private final JwtService jwtService = new JwtService(30);

    @AfterEach
    void limpiarContexto() {
        SecurityContextHolder.clearContext();
    }

    private String accessToken() {
        return jwtService.generarToken("juan", 1, "Juan", "Perez", "j@x.com", "V-1", Set.of("ROLE_USER"));
    }

    @Test
    void unRefreshOpacoEsRechazadoComoAccess() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer un-token-opaco-que-no-es-jwt");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (rq, rs) -> { });

        assertThat(response.getStatus()).isEqualTo(401);
    }

    @Test
    void accessTokenPorHeaderAutentica() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + accessToken());
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean continuo = new AtomicBoolean(false);
        FilterChain chain = (rq, rs) -> continuo.set(true);

        filter.doFilter(request, response, chain);

        assertThat(continuo).isTrue();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("juan");
    }

    @Test
    void accessTokenPorCookieAutentica() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("jwt", accessToken()));
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean continuo = new AtomicBoolean(false);

        filter.doFilter(request, response, (rq, rs) -> continuo.set(true));

        assertThat(continuo).isTrue();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("juan");
    }

    @Test
    void sinTokenContinuaSinAutenticar() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean continuo = new AtomicBoolean(false);

        filter.doFilter(request, response, (rq, rs) -> continuo.set(true));

        assertThat(continuo).isTrue();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
}
