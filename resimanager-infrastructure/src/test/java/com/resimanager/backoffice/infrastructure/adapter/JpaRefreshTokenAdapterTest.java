package com.resimanager.backoffice.infrastructure.adapter;

import com.resimanager.backoffice.domain.model.RefreshToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=none")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import(JpaRefreshTokenAdapter.class)
@Sql(scripts = "/refresh-token-schema.sql")
class JpaRefreshTokenAdapterTest {

    private static final OffsetDateTime AHORA = OffsetDateTime.now();

    @Autowired
    private JpaRefreshTokenAdapter adapter;

    private RefreshToken nuevoToken(String hash, UUID familyId, OffsetDateTime expira) {
        RefreshToken token = new RefreshToken();
        token.setTokenHash(hash);
        token.setFamilyId(familyId);
        token.setPersonaId(1);
        token.setExpira(expira);
        token.setRevocado("N");
        token.setUsrCrea(1);
        token.setFchHorCrea(AHORA);
        token.setEstCrea("TEST");
        token.setUsrMod(1);
        token.setFchHorMod(AHORA);
        token.setEstMod("TEST");
        return token;
    }

    @Test
    void guardaYBuscaPorHash() {
        UUID family = UUID.randomUUID();
        adapter.guardar(nuevoToken("hash-guarda", family, AHORA.plusDays(7)));

        Optional<RefreshToken> encontrado = adapter.buscarPorHash("hash-guarda");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getFamilyId()).isEqualTo(family);
        assertThat(adapter.buscarPorHash("no-existe")).isEmpty();
    }

    @Test
    void revocaTodaLaFamilia() {
        UUID family = UUID.randomUUID();
        adapter.guardar(nuevoToken("hash-f1", family, AHORA.plusDays(7)));
        adapter.guardar(nuevoToken("hash-f2", family, AHORA.plusDays(7)));
        adapter.guardar(nuevoToken("hash-otra", UUID.randomUUID(), AHORA.plusDays(7)));

        int afectados = adapter.revocarFamilia(family);

        assertThat(afectados).isEqualTo(2);
        assertThat(adapter.buscarPorHash("hash-f1").get().estaRevocado()).isTrue();
        assertThat(adapter.buscarPorHash("hash-f2").get().estaRevocado()).isTrue();
        assertThat(adapter.buscarPorHash("hash-otra").get().estaRevocado()).isFalse();
    }

    @Test
    void consumeMarcaElToken() {
        RefreshToken guardado = adapter.guardar(nuevoToken("hash-consume", UUID.randomUUID(), AHORA.plusDays(7)));
        guardado.setConsumido(AHORA);
        adapter.guardar(guardado);

        assertThat(adapter.buscarPorHash("hash-consume").get().estaConsumido()).isTrue();
    }

    @Test
    void eliminaSoloLosExpirados() {
        adapter.guardar(nuevoToken("hash-vigente", UUID.randomUUID(), AHORA.plusDays(1)));
        adapter.guardar(nuevoToken("hash-vencido", UUID.randomUUID(), AHORA.minusDays(1)));

        int eliminados = adapter.eliminarExpirados(AHORA);

        assertThat(eliminados).isEqualTo(1);
        assertThat(adapter.buscarPorHash("hash-vencido")).isEmpty();
        assertThat(adapter.buscarPorHash("hash-vigente")).isPresent();
    }
}
