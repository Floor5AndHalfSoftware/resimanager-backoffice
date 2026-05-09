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
@Table(name = "\"PerfAdministradora\"")
public class PerfAdministradora {
    @EmbeddedId
    private PerfAdministradoraId id;

    @MapsId("pfaPrfid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "pfa_prf_id", nullable = false)
    private Perfil pfaPrfid;

    @NotNull
    @Column(name = "pfaid", nullable = false)
    private Integer pfaid;

    @Size(max = 1)
    @NotNull
    @Column(name = "pfa_sts", nullable = false, length = 1)
    private String pFASts;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "pfa_usr_crea", nullable = false)
    private Persona pfaUsrcrea;

    @NotNull
    @Column(name = "pfa_fch_hor_crea", nullable = false)
    private OffsetDateTime pFAFchHorCrea;

    @Size(max = 40)
    @NotNull
    @Column(name = "pfa_est_crea", nullable = false, length = 40)
    private String pFAEstCrea;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "pfa_usr_mod", nullable = false)
    private Persona pfaUsrmod;

    @NotNull
    @Column(name = "pfa_fch_hor_mod", nullable = false)
    private OffsetDateTime pFAFchHorMod;

    @Size(max = 40)
    @NotNull
    @Column(name = "pfa_est_mod", nullable = false, length = 40)
    private String pFAEstMod;

}