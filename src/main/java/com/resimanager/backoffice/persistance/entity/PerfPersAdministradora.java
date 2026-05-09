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
@Table(name = "\"PerfPersAdministradora\"")
public class PerfPersAdministradora {
    @EmbeddedId
    private PerfPersAdministradoraId id;

    @MapsId("ppaPrfid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "ppa_prf_id", nullable = false)
    private Perfil ppaPrfid;

    @NotNull
    @Column(name = "ppaid", nullable = false)
    private Integer ppaid;

    @Size(max = 1)
    @NotNull
    @Column(name = "ppa_sts", nullable = false, length = 1)
    private String pPASts;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "ppa_usr_crea", nullable = false)
    private Persona ppaUsrcrea;

    @NotNull
    @Column(name = "ppa_fch_hor_crea", nullable = false)
    private OffsetDateTime pPAFchHorCrea;

    @Size(max = 40)
    @NotNull
    @Column(name = "ppa_est_crea", nullable = false, length = 40)
    private String pPAEstCrea;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "ppa_usr_mod", nullable = false)
    private Persona ppaUsrmod;

    @NotNull
    @Column(name = "ppa_fch_hor_mod", nullable = false)
    private OffsetDateTime pPAFchHorMod;

    @Size(max = 40)
    @NotNull
    @Column(name = "ppa_est_mod", nullable = false, length = 40)
    private String pPAEstMod;

}