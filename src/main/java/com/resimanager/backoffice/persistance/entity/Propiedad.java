package com.resimanager.backoffice.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "\"Propiedad\"")
public class Propiedad {
    @EmbeddedId
    private PropiedadId id;

    @Size(max = 80)
    @NotNull
    @Column(name = "PpdNombre", nullable = false, length = 80)
    private String ppdNombre;

    @NotNull
    @Column(name = "Ppd_UbiID", nullable = false)
    private Integer ppdUbiid;

    @Size(max = 1)
    @NotNull
    @Column(name = "PpdSts", nullable = false, length = 1)
    private String ppdSts;

    @NotNull
    @Column(name = "PpdFchHorCrea", nullable = false)
    private OffsetDateTime ppdFchHorCrea;

    @Size(max = 40)
    @NotNull
    @Column(name = "PpdEstCrea", nullable = false, length = 40)
    private String ppdEstCrea;

    @NotNull
    @Column(name = "PpdFchHorMod", nullable = false)
    private OffsetDateTime ppdFchHorMod;

    @Size(max = 40)
    @NotNull
    @Column(name = "PpdEstMod", nullable = false, length = 40)
    private String ppdEstMod;

}