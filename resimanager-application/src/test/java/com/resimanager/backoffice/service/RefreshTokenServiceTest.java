package com.resimanager.backoffice.service;

import com.resimanager.backoffice.domain.model.ContextoRefresh;
import com.resimanager.backoffice.domain.model.RefreshToken;
import com.resimanager.backoffice.domain.model.ResultadoRotacionRefresh;
import com.resimanager.backoffice.domain.port.out.RefreshTokenRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenServiceTest {

    private static final long TTL = 3600L;

    private final RefreshTokenGenerator generator = new RefreshTokenGenerator();
    private final FakeRefreshTokenRepository repository = new FakeRefreshTokenRepository();
    private RefreshTokenService service;

    @BeforeEach
    void setUp() {
        service = new RefreshTokenService(repository, generator);
        ReflectionTestUtils.setField(service, "refreshTokenTtlSeconds", TTL);
        repository.limpiar();
    }

    @Test
    void emiteTokenYGuardaSoloElHash() {
        ResultadoRotacionRefresh resultado = service.emitirNuevo(1, ContextoRefresh.ninguno());

        String hashEsperado = generator.hash(resultado.refreshToken());
        assertThat(repository.buscarPorHash(hashEsperado)).isPresent();
        // El valor en claro nunca se persiste
        assertThat(repository.valoresAlmacenados()).doesNotContain(resultado.refreshToken());
    }

    @Test
    void rotarConsumeElTokenAnteriorYEmiteUnoNuevo() {
        ResultadoRotacionRefresh primera = service.emitirNuevo(1, ContextoRefresh.ninguno());

        Optional<ResultadoRotacionRefresh> rotada = service.rotar(primera.refreshToken(), null);

        assertThat(rotada).isPresent();
        assertThat(rotada.get().refreshToken()).isNotEqualTo(primera.refreshToken());
        assertThat(repository.buscarPorHash(generator.hash(primera.refreshToken())).get().estaConsumido()).isTrue();
    }

    @Test
    void reutilizarUnTokenRotadoRevocaLaFamilia() {
        ResultadoRotacionRefresh primera = service.emitirNuevo(1, ContextoRefresh.ninguno());
        ResultadoRotacionRefresh segunda = service.rotar(primera.refreshToken(), null).orElseThrow();

        // Reutilización del token ya rotado
        assertThat(service.rotar(primera.refreshToken(), null)).isEmpty();
        // La familia completa quedó revocada, incluido el token vigente
        assertThat(repository.buscarPorHash(generator.hash(segunda.refreshToken())).get().estaRevocado()).isTrue();
        assertThat(service.rotar(segunda.refreshToken(), null)).isEmpty();
    }

    @Test
    void unTokenExpiradoNoSeRenueva() {
        ResultadoRotacionRefresh emitido = service.emitirNuevo(1, ContextoRefresh.ninguno());
        RefreshToken almacenado = repository.buscarPorHash(generator.hash(emitido.refreshToken())).orElseThrow();
        almacenado.setExpira(OffsetDateTime.now().minusHours(1));
        repository.guardar(almacenado);

        assertThat(service.rotar(emitido.refreshToken(), null)).isEmpty();
    }

    @Test
    void laRotacionNoExtiendeLaExpiracionAbsoluta() {
        ResultadoRotacionRefresh primera = service.emitirNuevo(1, ContextoRefresh.ninguno());
        OffsetDateTime expiraOriginal = repository.buscarPorHash(generator.hash(primera.refreshToken()))
                .orElseThrow().getExpira();

        ResultadoRotacionRefresh segunda = service.rotar(primera.refreshToken(), null).orElseThrow();

        assertThat(repository.buscarPorHash(generator.hash(segunda.refreshToken()))
                .orElseThrow().getExpira()).isEqualTo(expiraOriginal);
    }

    @Test
    void preservaYActualizaElContexto() {
        ResultadoRotacionRefresh emitido = service.emitirNuevo(1, new ContextoRefresh("ADMINISTRADORA", 1, 2));

        ResultadoRotacionRefresh renovado = service.rotar(emitido.refreshToken(), null).orElseThrow();
        assertThat(renovado.contextoTipo()).isEqualTo("ADMINISTRADORA");
        assertThat(renovado.contextoEntidadId()).isEqualTo(1);
        assertThat(renovado.contextoPerfilId()).isEqualTo(2);

        ResultadoRotacionRefresh cambiado = service.rotar(renovado.refreshToken(),
                new ContextoRefresh("CONJUNTO", 5, 7)).orElseThrow();
        assertThat(cambiado.contextoTipo()).isEqualTo("CONJUNTO");
        assertThat(cambiado.contextoEntidadId()).isEqualTo(5);
        assertThat(cambiado.contextoPerfilId()).isEqualTo(7);
    }

    @Test
    void revocarInvalidaElTokenYSuFamilia() {
        ResultadoRotacionRefresh emitido = service.emitirNuevo(1, ContextoRefresh.ninguno());

        service.revocar(emitido.refreshToken());

        assertThat(service.rotar(emitido.refreshToken(), null)).isEmpty();
    }

    static class FakeRefreshTokenRepository implements RefreshTokenRepositoryPort {
        private final Map<String, RefreshToken> porHash = new LinkedHashMap<>();
        private int secuencia = 0;

        void limpiar() {
            porHash.clear();
            secuencia = 0;
        }

        Iterable<String> valoresAlmacenados() {
            return porHash.keySet();
        }

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
