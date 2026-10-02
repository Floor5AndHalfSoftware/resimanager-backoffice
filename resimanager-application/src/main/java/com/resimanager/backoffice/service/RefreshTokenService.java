package com.resimanager.backoffice.service;

import com.resimanager.backoffice.domain.model.ContextoRefresh;
import com.resimanager.backoffice.domain.model.RefreshToken;
import com.resimanager.backoffice.domain.model.ResultadoRotacionRefresh;
import com.resimanager.backoffice.domain.port.in.RefreshTokenUseCase;
import com.resimanager.backoffice.domain.port.out.RefreshTokenRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementación del ciclo de vida del refresh token: emisión, rotación con
 * detección de reutilización y revocación.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService implements RefreshTokenUseCase {

    private static final String EST_ISSUE = "REFRESH-ISSUE";
    private static final String EST_ROTATE = "REFRESH-ROTATE";
    private static final String EST_EXPIRE = "REFRESH-EXPIRE";
    private static final String COD_ADMINISTRADORA = "A";
    private static final String COD_CONJUNTO = "C";
    private static final String TIPO_ADMINISTRADORA = "ADMINISTRADORA";
    private static final String TIPO_CONJUNTO = "CONJUNTO";

    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final RefreshTokenGenerator refreshTokenGenerator;

    @Value("${app.security.refresh-token-ttl-seconds:604800}")
    private long refreshTokenTtlSeconds;

    @Override
    @Transactional
    public ResultadoRotacionRefresh emitirNuevo(Integer personaId, ContextoRefresh contexto) {
        OffsetDateTime ahora = OffsetDateTime.now();
        String tokenPlano = refreshTokenGenerator.generarToken();

        RefreshToken token = new RefreshToken();
        token.setTokenHash(refreshTokenGenerator.hash(tokenPlano));
        token.setFamilyId(UUID.randomUUID());
        token.setPersonaId(personaId);
        aplicarContexto(token, contexto);
        token.setExpira(ahora.plusSeconds(refreshTokenTtlSeconds));
        token.setRevocado("N");
        token.setUsrCrea(personaId);
        token.setFchHorCrea(ahora);
        token.setEstCrea(EST_ISSUE);
        token.setUsrMod(personaId);
        token.setFchHorMod(ahora);
        token.setEstMod(EST_ISSUE);

        refreshTokenRepositoryPort.guardar(token);
        log.debug("Refresh emitido para persona {} (familia {})", personaId, token.getFamilyId());

        return new ResultadoRotacionRefresh(tokenPlano, personaId,
                tipoResultado(token), token.getContextoEntidadId(), token.getContextoPerfilId());
    }

    @Override
    @Transactional
    public Optional<ResultadoRotacionRefresh> rotar(String tokenPlano, ContextoRefresh nuevoContexto) {
        if (tokenPlano == null || tokenPlano.isBlank()) {
            return Optional.empty();
        }

        Optional<RefreshToken> encontrado = refreshTokenRepositoryPort.buscarPorHash(refreshTokenGenerator.hash(tokenPlano));
        if (encontrado.isEmpty()) {
            return Optional.empty();
        }

        RefreshToken actual = encontrado.get();
        OffsetDateTime ahora = OffsetDateTime.now();

        if (actual.estaRevocado()) {
            return Optional.empty();
        }

        // Reutilización de un token ya rotado: se revoca toda la familia.
        if (actual.estaConsumido()) {
            refreshTokenRepositoryPort.revocarFamilia(actual.getFamilyId());
            log.warn("Reutilización de refresh detectada; familia {} revocada", actual.getFamilyId());
            return Optional.empty();
        }

        if (actual.getExpira().isBefore(ahora)) {
            actual.setRevocado("S");
            actual.setEstMod(EST_EXPIRE);
            actual.setFchHorMod(ahora);
            refreshTokenRepositoryPort.guardar(actual);
            return Optional.empty();
        }

        // Consumir el token presentado (rotación).
        actual.setConsumido(ahora);
        actual.setEstMod(EST_ROTATE);
        actual.setFchHorMod(ahora);
        refreshTokenRepositoryPort.guardar(actual);

        // El nuevo token conserva la expiración absoluta de la familia (no la extiende).
        ContextoRefresh contexto = (nuevoContexto != null && nuevoContexto.presente())
                ? nuevoContexto
                : new ContextoRefresh(actual.getContextoTipo(), actual.getContextoEntidadId(), actual.getContextoPerfilId());

        String tokenPlanoNuevo = refreshTokenGenerator.generarToken();
        RefreshToken nuevo = new RefreshToken();
        nuevo.setTokenHash(refreshTokenGenerator.hash(tokenPlanoNuevo));
        nuevo.setFamilyId(actual.getFamilyId());
        nuevo.setPersonaId(actual.getPersonaId());
        aplicarContexto(nuevo, contexto);
        nuevo.setExpira(actual.getExpira());
        nuevo.setRevocado("N");
        nuevo.setUsrCrea(actual.getPersonaId());
        nuevo.setFchHorCrea(ahora);
        nuevo.setEstCrea(EST_ROTATE);
        nuevo.setUsrMod(actual.getPersonaId());
        nuevo.setFchHorMod(ahora);
        nuevo.setEstMod(EST_ROTATE);

        refreshTokenRepositoryPort.guardar(nuevo);

        return Optional.of(new ResultadoRotacionRefresh(tokenPlanoNuevo, nuevo.getPersonaId(),
                tipoResultado(nuevo), nuevo.getContextoEntidadId(), nuevo.getContextoPerfilId()));
    }

    @Override
    @Transactional
    public void revocar(String tokenPlano) {
        if (tokenPlano == null || tokenPlano.isBlank()) {
            return;
        }
        refreshTokenRepositoryPort.buscarPorHash(refreshTokenGenerator.hash(tokenPlano))
                .ifPresent(token -> {
                    refreshTokenRepositoryPort.revocarFamilia(token.getFamilyId());
                    log.debug("Familia de refresh revocada en logout: {}", token.getFamilyId());
                });
    }

    @Override
    @Transactional
    public int eliminarExpirados() {
        return refreshTokenRepositoryPort.eliminarExpirados(OffsetDateTime.now());
    }

    private void aplicarContexto(RefreshToken token, ContextoRefresh contexto) {
        if (contexto == null || !contexto.presente()) {
            token.setContextoTipo(null);
            token.setContextoEntidadId(null);
            token.setContextoPerfilId(null);
            return;
        }
        token.setContextoTipo(aCodigo(contexto.tipo()));
        token.setContextoEntidadId(contexto.entidadId());
        token.setContextoPerfilId(contexto.perfilId());
    }

    private String aCodigo(String tipo) {
        return TIPO_CONJUNTO.equals(tipo) ? COD_CONJUNTO : COD_ADMINISTRADORA;
    }

    private String aTipo(String codigo) {
        return COD_CONJUNTO.equals(codigo) ? TIPO_CONJUNTO : TIPO_ADMINISTRADORA;
    }

    private String tipoResultado(RefreshToken token) {
        return token.getContextoTipo() == null ? null : aTipo(token.getContextoTipo());
    }
}
