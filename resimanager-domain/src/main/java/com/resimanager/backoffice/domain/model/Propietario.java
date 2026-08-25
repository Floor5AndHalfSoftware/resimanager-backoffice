package com.resimanager.backoffice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "\"Propietario\"")
public class Propietario {
    @EmbeddedId
    private PropietarioId id;

    @NotNull
    @Column(name = "PptID", nullable = false)
    private Integer pptID;

    @NotNull
    @Column(name = "PptFchDesde", nullable = false)
    private LocalDate pptFchDesde;

    @Column(name = "PptFchHasta")
    private LocalDate pptFchHasta;

    @Size(max = 1)
    @NotNull
    @Column(name = "PptSts", nullable = false, length = 1)
    private String pptSts;

    @NotNull
    @Column(name = "PptFchHorCrea", nullable = false)
    private OffsetDateTime pptFchHorCrea;

    @Size(max = 40)
    @NotNull
    @Column(name = "PptEstCrea", nullable = false, length = 40)
    private String pptEstCrea;

    @NotNull
    @Column(name = "PptFchHorMod", nullable = false)
    private OffsetDateTime pptFchHorMod;

    @Size(max = 40)
    @NotNull
    @Column(name = "PptEstMod", nullable = false, length = 40)
    private String pptEstMod;

}