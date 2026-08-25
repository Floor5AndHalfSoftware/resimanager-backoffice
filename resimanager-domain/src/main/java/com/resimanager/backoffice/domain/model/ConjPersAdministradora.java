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
@Table(name = "\"ConjPersAdministradora\"")
public class ConjPersAdministradora {
    @EmbeddedId
    private ConjPersAdministradoraId id;

    @MapsId("cpaConjid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "cpa_conj_id", nullable = false)
    private Conjunto cpaConjid;

    @NotNull
    @Column(name = "cpaid", nullable = false)
    private Integer cpaid;

    @Size(max = 1)
    @NotNull
    @Column(name = "cpa_sts", nullable = false, length = 1)
    private String cPASts;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "cpa_usr_crea", nullable = false)
    private Persona cpaUsrcrea;

    @NotNull
    @Column(name = "cpa_fch_hor_crea", nullable = false)
    private OffsetDateTime cPAFchHorCrea;

    @Size(max = 40)
    @NotNull
    @Column(name = "cpa_est_crea", nullable = false, length = 40)
    private String cPAEstCrea;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "cpa_usr_mod", nullable = false)
    private Persona cpaUsrmod;

    @NotNull
    @Column(name = "cpa_fch_hor_mod", nullable = false)
    private OffsetDateTime cPAFchHorMod;

    @Size(max = 40)
    @NotNull
    @Column(name = "cpa_est_mod", nullable = false, length = 40)
    private String cPAEstMod;

}