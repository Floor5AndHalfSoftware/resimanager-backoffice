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

/**
 * Token de restablecimiento de contraseña. Solo se persiste el hash SHA-256
 * del token opaco; es de un solo uso y caduca en un tiempo corto.
 */
@Getter
@Setter
@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prt_id", nullable = false)
    private Integer id;

    @Size(max = 64)
    @NotNull
    @Column(name = "prt_token_hash", nullable = false, length = 64)
    private String tokenHash;

    @NotNull
    @Column(name = "prt_per_id", nullable = false)
    private Integer personaId;

    @NotNull
    @Column(name = "prt_expira", nullable = false)
    private OffsetDateTime expira;

    @Column(name = "prt_usado")
    private OffsetDateTime usado;

    @NotNull
    @Column(name = "prt_usr_crea", nullable = false)
    private Integer usrCrea;

    @NotNull
    @Column(name = "prt_fch_hor_crea", nullable = false)
    private OffsetDateTime fchHorCrea;

    @Size(max = 40)
    @NotNull
    @Column(name = "prt_est_crea", nullable = false, length = 40)
    private String estCrea;

    @NotNull
    @Column(name = "prt_usr_mod", nullable = false)
    private Integer usrMod;

    @NotNull
    @Column(name = "prt_fch_hor_mod", nullable = false)
    private OffsetDateTime fchHorMod;

    @Size(max = 40)
    @NotNull
    @Column(name = "prt_est_mod", nullable = false, length = 40)
    private String estMod;

    public boolean estaUsado() {
        return usado != null;
    }

    public boolean estaExpirado(OffsetDateTime momento) {
        return expira != null && expira.isBefore(momento);
    }
}
