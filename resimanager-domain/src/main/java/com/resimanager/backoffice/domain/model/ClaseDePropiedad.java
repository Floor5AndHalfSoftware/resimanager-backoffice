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

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "\"ClaseDePropiedad\"")
public class ClaseDePropiedad {
    @EmbeddedId
    private ClaseDePropiedadId id;

    @MapsId("cdpConjid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "Cdp_ConjID", nullable = false)
    private Conjunto cdpConjid;

    @Size(max = 80)
    @NotNull
    @Column(name = "CdpNombre", nullable = false, length = 80)
    private String cdpNombre;

    @Size(max = 120)
    @Column(name = "CdpDescrip", length = 120)
    private String cdpDescrip;

    @NotNull
    @Column(name = "CdpAreaM2", nullable = false, precision = 10, scale = 4)
    private BigDecimal cdpAreaM2;

    @Size(max = 1)
    @NotNull
    @Column(name = "CdpSts", nullable = false, length = 1)
    private String cdpSts;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "Cdp_UsrCrea", nullable = false)
    private Persona cdpUsrcrea;

    @NotNull
    @Column(name = "CdpFchHorCrea", nullable = false)
    private OffsetDateTime cdpFchHorCrea;

    @Size(max = 40)
    @NotNull
    @Column(name = "CdpEstCrea", nullable = false, length = 40)
    private String cdpEstCrea;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "Cdp_UsrMod", nullable = false)
    private Persona cdpUsrmod;

    @NotNull
    @Column(name = "CdpFchHorMod", nullable = false)
    private OffsetDateTime cdpFchHorMod;

    @Size(max = 40)
    @NotNull
    @Column(name = "CdpEstMod", nullable = false, length = 40)
    private String cdpEstMod;

}