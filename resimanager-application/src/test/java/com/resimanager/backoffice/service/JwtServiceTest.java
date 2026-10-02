package com.resimanager.backoffice.service;

import com.resimanager.backoffice.dto.ContextoActualDTO;
import com.resimanager.backoffice.dto.UserInfoDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Set;

import static com.resimanager.backoffice.utils.Constants.SUPER_SECRET_KEY;
import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static SecretKey signingKey() throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        byte[] hash = digest.digest(SUPER_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        return Keys.hmacShaKeyFor(hash);
    }

    private static Claims parse(String token) throws Exception {
        return Jwts.parser().verifyWith(signingKey()).build().parseSignedClaims(token).getPayload();
    }

    @Test
    void incluyeClaimTokenTypeAccess() throws Exception {
        JwtService service = new JwtService(30);

        String token = service.generarToken("juan", 1, "Juan", "Perez", "j@x.com", "V-1", Set.of("ROLE_USER"));

        assertThat(parse(token).get("tokenType")).isEqualTo("access");
    }

    @Test
    void usaLaVigenciaConfigurada() throws Exception {
        JwtService service = new JwtService(60);

        String token = service.generarToken("juan", 1, "Juan", "Perez", "j@x.com", "V-1", Set.of("ROLE_USER"));
        Claims claims = parse(token);

        long segundos = (claims.getExpiration().getTime() - claims.getIssuedAt().getTime()) / 1000;
        assertThat(segundos).isBetween(3590L, 3610L);
    }

    @Test
    void preservaElContextoEnElToken() throws Exception {
        JwtService service = new JwtService(30);
        UserInfoDTO userInfo = UserInfoDTO.builder().id(7).usuario("maria").nombre("Maria")
                .apellido("Gomez").email("m@x.com").documento("V-9").build();
        ContextoActualDTO contexto = ContextoActualDTO.builder()
                .tipo("CONJUNTO").entidadId(5).perfilId(3).build();

        String token = service.generarTokenConContexto(userInfo, contexto, List.of("ROLE_USER"));
        Claims claims = parse(token);

        assertThat(claims.get("contextoTipo")).isEqualTo("CONJUNTO");
        assertThat(claims.get("contextoEntidadId")).isEqualTo(5);
        assertThat(claims.get("contextoPerfilId")).isEqualTo(3);
        assertThat(claims.get("userId")).isEqualTo(7);
    }
}
