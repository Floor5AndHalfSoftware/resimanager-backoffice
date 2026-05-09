package com.resimanager.backoffice.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "\"PerfConjunto\"")
public class PerfConjunto {
    @EmbeddedId
    private PerfConjuntoId id;

    @MapsId("pfcPrfid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "pfc_prf_id", nullable = false)
    private Perfil pfcPrfid;

    @NotNull
    @Column(name = "pfcid", nullable = false)
    private Integer pfcid;

    @Size(max = 1)
    @NotNull
    @Column(name = "pfc_sts", nullable = false, length = 1)
    private String pFCSts;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "pfc_usr_crea", nullable = false)
    private Persona pfcUsrcrea;

    @NotNull
    @Column(name = "pfc_fch_hor_crea", nullable = false)
    private OffsetDateTime pFCFchHorCrea;

    @Size(max = 40)
    @NotNull
    @Column(name = "pfc_est_crea", nullable = false, length = 40)
    private String pFCEstCrea;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "pfc_usr_mod", nullable = false)
    private Persona pfcUsrmod;

    @NotNull
    @Column(name = "pfc_fch_hor_mod", nullable = false)
    private OffsetDateTime pFCFchHorMod;

    @Size(max = 40)
    @NotNull
    @Column(name = "pfc_est_mod", nullable = false, length = 40)
    private String pFCEstMod;

}