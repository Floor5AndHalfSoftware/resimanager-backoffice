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
@Table(name = "\"PerfPersConjunto\"")
public class PerfPersConjunto {
    @EmbeddedId
    private PerfPersConjuntoId id;

    @MapsId("ppcPrfid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "ppc_prf_id", nullable = false)
    private Perfil ppcPrfid;

    @NotNull
    @Column(name = "ppcid", nullable = false)
    private Integer ppcid;

    @Size(max = 1)
    @NotNull
    @Column(name = "ppc_sts", nullable = false, length = 1)
    private String pPCSts;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "ppc_usr_crea", nullable = false)
    private Persona ppcUsrcrea;

    @NotNull
    @Column(name = "ppc_fch_hor_crea", nullable = false)
    private OffsetDateTime pPCFchHorCrea;

    @Size(max = 40)
    @NotNull
    @Column(name = "ppc_est_crea", nullable = false, length = 40)
    private String pPCEstCrea;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "ppc_usr_mod", nullable = false)
    private Persona ppcUsrmod;

    @NotNull
    @Column(name = "ppc_fch_hor_mod", nullable = false)
    private OffsetDateTime pPCFchHorMod;

    @Size(max = 40)
    @NotNull
    @Column(name = "ppc_est_mod", nullable = false, length = 40)
    private String pPCEstMod;

}