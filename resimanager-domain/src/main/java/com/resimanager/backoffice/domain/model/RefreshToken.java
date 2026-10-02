package com.resimanager.backoffice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Refresh token de sesión. Se persiste únicamente el hash SHA-256 del token opaco.
 * Pertenece a una familia (sesión) y arrastra el contexto activo para poder
 * reconstruir el access token al renovar.
 */
@Getter
@Setter
@Entity
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rt_id", nullable = false)
    private Integer id;

    @Size(max = 64)
    @NotNull
    @Column(name = "rt_token_hash", nullable = false, length = 64)
    private String tokenHash;

    @NotNull
    @Column(name = "rt_family_id", nullable = false)
    private UUID familyId;

    @NotNull
    @Column(name = "rt_per_id", nullable = false)
    private Integer personaId;

    @Size(max = 1)
    @Column(name = "rt_ctx_tipo", length = 1)
    private String contextoTipo;

    @Column(name = "rt_ctx_entidad_id")
    private Integer contextoEntidadId;

    @Column(name = "rt_ctx_prf_id")
    private Integer contextoPerfilId;

    @NotNull
    @Column(name = "rt_expira", nullable = false)
    private OffsetDateTime expira;

    @Column(name = "rt_consumido")
    private OffsetDateTime consumido;

    @Size(max = 1)
    @NotNull
    @Column(name = "rt_revocado", nullable = false, length = 1)
    private String revocado;

    @NotNull
    @Column(name = "rt_usr_crea", nullable = false)
    private Integer usrCrea;

    @NotNull
    @Column(name = "rt_fch_hor_crea", nullable = false)
    private OffsetDateTime fchHorCrea;

    @Size(max = 40)
    @NotNull
    @Column(name = "rt_est_crea", nullable = false, length = 40)
    private String estCrea;

    @NotNull
    @Column(name = "rt_usr_mod", nullable = false)
    private Integer usrMod;

    @NotNull
    @Column(name = "rt_fch_hor_mod", nullable = false)
    private OffsetDateTime fchHorMod;

    @Size(max = 40)
    @NotNull
    @Column(name = "rt_est_mod", nullable = false, length = 40)
    private String estMod;

    /** Estatus de revocado (S) frente a vigente (N). */
    public boolean estaRevocado() {
        return "S".equals(revocado);
    }

    /** Indica si el token ya fue rotado. */
    public boolean estaConsumido() {
        return consumido != null;
    }
}
