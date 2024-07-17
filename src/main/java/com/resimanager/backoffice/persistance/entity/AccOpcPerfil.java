package com.resimanager.backoffice.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "AccOpcPerfil")
public class AccOpcPerfil {
    @EmbeddedId
    private AccOpcPerfilId id;

    @NotNull
    @Column(name = "AOPID", nullable = false)
    private Integer aopid;

    @Size(max = 1)
    @NotNull
    @Column(name = "AOPSts", nullable = false, length = 1)
    private String aOPSts;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "AOP_UsrCrea", nullable = false)
    private Persona aopUsrcrea;

    @NotNull
    @Column(name = "AOPFchHorCrea", nullable = false)
    private OffsetDateTime aOPFchHorCrea;

    @Size(max = 40)
    @NotNull
    @Column(name = "AOPEstCrea", nullable = false, length = 40)
    private String aOPEstCrea;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "AOP_UsrMod", nullable = false)
    private Persona aopUsrmod;

    @NotNull
    @Column(name = "AOPFchHorMod", nullable = false)
    private OffsetDateTime aOPFchHorMod;

    @Size(max = 40)
    @NotNull
    @Column(name = "AOPEstMod", nullable = false, length = 40)
    private String aOPEstMod;

}