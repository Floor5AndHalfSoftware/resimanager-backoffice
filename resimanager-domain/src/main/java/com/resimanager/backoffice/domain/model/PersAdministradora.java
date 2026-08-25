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
@Table(name = "\"PersAdministradora\"")
public class PersAdministradora {
    @EmbeddedId
    private PersAdministradoraId id;

    @MapsId("paPerid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "pa_per_id", nullable = false)
    private Persona paPerid;

    @NotNull
    @Column(name = "paid", nullable = false)
    private Integer paid;

    @Size(max = 1)
    @NotNull
    @Column(name = "pa_sts", nullable = false, length = 1)
    private String pASts;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "pa_usr_crea", nullable = false)
    private Persona paUsrcrea;

    @NotNull
    @Column(name = "pa_fch_hor_crea", nullable = false)
    private OffsetDateTime pAFchHorCrea;

    @Size(max = 40)
    @NotNull
    @Column(name = "pa_est_crea", nullable = false, length = 40)
    private String pAEstCrea;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "pa_usr_mod", nullable = false)
    private Persona paUsrmod;

    @NotNull
    @Column(name = "pa_fch_hor_mod", nullable = false)
    private OffsetDateTime pAFchHorMod;

    @Size(max = 40)
    @NotNull
    @Column(name = "pa_est_mod", nullable = false, length = 40)
    private String pAEstMod;

}