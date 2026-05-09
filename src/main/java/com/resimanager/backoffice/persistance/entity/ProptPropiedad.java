package com.resimanager.backoffice.persistance.entity;

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
@Table(name = "\"ProptPropiedad\"")
public class ProptPropiedad {
    @EmbeddedId
    private ProptPropiedadId id;

    @NotNull
    @Column(name = "PPID", nullable = false)
    private Integer ppid;

    @NotNull
    @Column(name = "PPFchDesde", nullable = false)
    private LocalDate pPFchDesde;

    @Column(name = "PPFchHasta")
    private LocalDate pPFchHasta;

    @Size(max = 1)
    @NotNull
    @Column(name = "PPSts", nullable = false, length = 1)
    private String pPSts;

    @NotNull
    @Column(name = "PPFchHorCrea", nullable = false)
    private OffsetDateTime pPFchHorCrea;

    @Size(max = 40)
    @NotNull
    @Column(name = "PPEstCrea", nullable = false, length = 40)
    private String pPEstCrea;

    @NotNull
    @Column(name = "PPFchHorMod", nullable = false)
    private OffsetDateTime pPFchHorMod;

    @Size(max = 40)
    @NotNull
    @Column(name = "PPEstMod", nullable = false, length = 40)
    private String pPEstMod;

}