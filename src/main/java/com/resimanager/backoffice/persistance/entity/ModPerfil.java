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
@Table(name = "\"ModPerfil\"")
public class ModPerfil {
    @EmbeddedId
    private ModPerfilId id;

    @MapsId("mpPrfid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "mp_prf_id", nullable = false)
    private Perfil mpPrfid;

    @MapsId("mpModid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "mp_mod_id", nullable = false)
    private Modulo mpModid;

    @NotNull
    @Column(name = "mpid", nullable = false)
    private Integer mpid;

    @Size(max = 1)
    @NotNull
    @Column(name = "mp_sts", nullable = false, length = 1)
    private String mPSts;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "mp_usr_crea", nullable = false)
    private Persona mpUsrcrea;

    @NotNull
    @Column(name = "mp_fch_hor_crea", nullable = false)
    private OffsetDateTime mPFchHorCrea;

    @Size(max = 40)
    @NotNull
    @Column(name = "mp_est_crea", nullable = false, length = 40)
    private String mPEstCrea;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "mp_usr_mod", nullable = false)
    private Persona mpUsrmod;

    @NotNull
    @Column(name = "mp_fch_hor_mod", nullable = false)
    private OffsetDateTime mPFchHorMod;

    @Size(max = 40)
    @NotNull
    @Column(name = "mp_est_mod", nullable = false, length = 40)
    private String mPEstMod;

}