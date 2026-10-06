package com.resimanager.backoffice.infrastructure.adapter;

import com.resimanager.backoffice.domain.model.PasswordResetToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=none")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import(JpaPasswordResetTokenAdapter.class)
@Sql(scripts = "/password-reset-token-schema.sql")
class JpaPasswordResetTokenAdapterTest {

    private static final OffsetDateTime AHORA = OffsetDateTime.now();

    @Autowired
    private JpaPasswordResetTokenAdapter adapter;

    private PasswordResetToken nuevo(String hash, Integer personaId, OffsetDateTime expira) {
        PasswordResetToken token = new PasswordResetToken();
        token.setTokenHash(hash);
        token.setPersonaId(personaId);
        token.setExpira(expira);
        token.setUsrCrea(personaId);
        token.setFchHorCrea(AHORA);
        token.setEstCrea("TEST");
        token.setUsrMod(personaId);
        token.setFchHorMod(AHORA);
        token.setEstMod("TEST");
        return token;
    }

    @Test
    void guardaYBuscaPorHash() {
        adapter.guardar(nuevo("hash-reset", 1, AHORA.plusMinutes(30)));

        assertThat(adapter.buscarPorHash("hash-reset")).isPresent();
        assertThat(adapter.buscarPorHash("no-existe")).isEmpty();
    }

    @Test
    void consumeMarcaUsado() {
        PasswordResetToken guardado = adapter.guardar(nuevo("hash-consume", 1, AHORA.plusMinutes(30)));
        guardado.setUsado(AHORA);
        adapter.guardar(guardado);

        assertThat(adapter.buscarPorHash("hash-consume").get().estaUsado()).isTrue();
    }

    @Test
    void invalidaTodosLosPendientesDeUnaPersona() {
        adapter.guardar(nuevo("hash-p1-a", 1, AHORA.plusMinutes(30)));
        adapter.guardar(nuevo("hash-p1-b", 1, AHORA.plusMinutes(30)));
        adapter.guardar(nuevo("hash-p2", 2, AHORA.plusMinutes(30)));

        int afectados = adapter.invalidarPorPersona(1);

        assertThat(afectados).isEqualTo(2);
        assertThat(adapter.buscarPorHash("hash-p1-a").get().estaUsado()).isTrue();
        assertThat(adapter.buscarPorHash("hash-p1-b").get().estaUsado()).isTrue();
        assertThat(adapter.buscarPorHash("hash-p2").get().estaUsado()).isFalse();
    }

    @Test
    void eliminaSoloLosExpirados() {
        adapter.guardar(nuevo("hash-vigente", 1, AHORA.plusMinutes(30)));
        adapter.guardar(nuevo("hash-vencido", 1, AHORA.minusMinutes(1)));

        int eliminados = adapter.eliminarExpirados(AHORA);

        assertThat(eliminados).isEqualTo(1);
        assertThat(adapter.buscarPorHash("hash-vencido")).isEmpty();
        assertThat(adapter.buscarPorHash("hash-vigente")).isPresent();
    }
}
