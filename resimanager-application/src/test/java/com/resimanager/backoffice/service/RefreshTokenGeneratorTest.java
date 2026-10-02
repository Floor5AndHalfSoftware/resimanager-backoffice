package com.resimanager.backoffice.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenGeneratorTest {

    private final RefreshTokenGenerator generator = new RefreshTokenGenerator();

    @Test
    void generaTokensAleatoriosYNoVacios() {
        String a = generator.generarToken();
        String b = generator.generarToken();

        assertThat(a).isNotBlank();
        assertThat(b).isNotBlank();
        assertThat(a).isNotEqualTo(b);
        // 32 bytes en base64url sin relleno -> 43 caracteres
        assertThat(a).hasSize(43);
    }

    @Test
    void elHashEsEstableYNoReversible() {
        String token = "un-token-opaco-de-prueba";
        String h1 = generator.hash(token);
        String h2 = generator.hash(token);

        assertThat(h1).isEqualTo(h2);
        assertThat(h1).hasSize(64);
        assertThat(h1).isNotEqualTo(token);
        assertThat(h1).doesNotContain(token);
    }

    @Test
    void tokensDistintosProducenHashesDistintos() {
        assertThat(generator.hash(generator.generarToken()))
                .isNotEqualTo(generator.hash(generator.generarToken()));
    }
}
