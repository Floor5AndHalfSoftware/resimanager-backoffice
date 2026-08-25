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

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "\"Propiedad\"")
public class Propiedad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ppid", nullable = false)
    private Integer ppid;

    @NotNull
    @Column(name = "pp_conj_id", nullable = false)
    private Integer ppConjId;

    @NotNull
    @Column(name = "pp_cdp_id", nullable = false)
    private Integer ppCdpId;

    @Size(max = 20)
    @NotNull
    @Column(name = "pp_numero", nullable = false, length = 20)
    private String ppNumero;

    @NotNull
    @Column(name = "pp_cantidad", nullable = false, precision = 10, scale = 4)
    private BigDecimal ppCantidad;

    @NotNull
    @Column(name = "pp_coef_participacion", nullable = false, precision = 10, scale = 8)
    private BigDecimal ppCoefParticipacion;

    @Size(max = 1)
    @NotNull
    @Column(name = "pp_sts", nullable = false, length = 1)
    private String ppSts;

    @NotNull
    @Column(name = "pp_usr_crea", nullable = false)
    private Integer ppUsrCrea;

    @NotNull
    @Column(name = "pp_fch_hor_crea", nullable = false)
    private OffsetDateTime ppFchHorCrea;

    @Size(max = 40)
    @NotNull
    @Column(name = "pp_est_crea", nullable = false, length = 40)
    private String ppEstCrea;

    @NotNull
    @Column(name = "pp_usr_mod", nullable = false)
    private Integer ppUsrMod;

    @NotNull
    @Column(name = "pp_fch_hor_mod", nullable = false)
    private OffsetDateTime ppFchHorMod;

    @Size(max = 40)
    @NotNull
    @Column(name = "pp_est_mod", nullable = false, length = 40)
    private String ppEstMod;
}
