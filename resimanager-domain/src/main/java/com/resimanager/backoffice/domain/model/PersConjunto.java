package com.resimanager.backoffice.domain.model;

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
@Table(name = "\"PersConjunto\"")
public class PersConjunto {
    @EmbeddedId
    private PersConjuntoId id;

    @MapsId("pcPerid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "pc_per_id", nullable = false)
    private Persona pcPerid;

    @NotNull
    @Column(name = "pcid", nullable = false)
    private Integer pcid;

    @Size(max = 1)
    @NotNull
    @Column(name = "pc_sts", nullable = false, length = 1)
    private String pCSts;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "pc_usr_crea", nullable = false)
    private Persona pcUsrcrea;

    @NotNull
    @Column(name = "pc_fch_hor_crea", nullable = false)
    private OffsetDateTime pCFchHorCrea;

    @Size(max = 40)
    @NotNull
    @Column(name = "pc_est_crea", nullable = false, length = 40)
    private String pCEstCrea;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "pc_usr_mod", nullable = false)
    private Persona pcUsrmod;

    @NotNull
    @Column(name = "pc_fch_hor_mod", nullable = false)
    private OffsetDateTime pCFchHorMod;

    @Size(max = 40)
    @NotNull
    @Column(name = "pc_est_mod", nullable = false, length = 40)
    private String pCEstMod;

}