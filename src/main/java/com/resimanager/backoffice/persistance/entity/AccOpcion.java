package com.resimanager.backoffice.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
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
@Table(name = "\"AccOpcion\"")
public class AccOpcion {
    @EmbeddedId
    private AccOpcionId id;

    @MapsId("id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "ao_mod_id", referencedColumnName = "opc_modid", nullable = false),
            @JoinColumn(name = "ao_opc_id", referencedColumnName = "opcid", nullable = false)
    })
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private Opcion opcion;

    @MapsId("aoAccid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "ao_acc_id", nullable = false)
    private Accion aoAccid;

    @NotNull
    @Column(name = "aoid", nullable = false)
    private Integer aoid;

    @NotNull
    @Column(name = "ao_cod_seg", nullable = false)
    private Integer aOCodSeg;

    @Size(max = 1)
    @NotNull
    @Column(name = "ao_sts", nullable = false, length = 1)
    private String aOSts;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "ao_usr_crea", nullable = false)
    private Persona aoUsrcrea;

    @NotNull
    @Column(name = "ao_fch_hor_crea", nullable = false)
    private OffsetDateTime aOFchHorCrea;

    @Size(max = 40)
    @NotNull
    @Column(name = "ao_est_crea", nullable = false, length = 40)
    private String aOEstCrea;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "ao_usr_mod", nullable = false)
    private Persona aoUsrmod;

    @NotNull
    @Column(name = "ao_fch_hor_mod", nullable = false)
    private OffsetDateTime aOFchHorMod;

    @Size(max = 40)
    @NotNull
    @Column(name = "ao_est_mod", nullable = false, length = 40)
    private String aOEstMod;

}