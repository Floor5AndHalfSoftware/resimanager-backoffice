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
@Table(name = "\"OpcPerfil\"")
public class OpcPerfil {
    @EmbeddedId
    private OpcPerfilId id;

    @MapsId("id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "op_mod_id", referencedColumnName = "opc_modid", nullable = false),
            @JoinColumn(name = "op_opc_id", referencedColumnName = "opcid", nullable = false)
    })
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private Opcion opcion;

    @NotNull
    @Column(name = "opid", nullable = false)
    private Integer opid;

    @Size(max = 1)
    @NotNull
    @Column(name = "op_sts", nullable = false, length = 1)
    private String oPSts;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "op_usr_crea", nullable = false)
    private Persona opUsrcrea;

    @NotNull
    @Column(name = "op_fch_hor_crea", nullable = false)
    private OffsetDateTime oPFchHorCrea;

    @Size(max = 40)
    @NotNull
    @Column(name = "op_est_crea", nullable = false, length = 40)
    private String oPEstCrea;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "op_usr_mod", nullable = false)
    private Persona opUsrmod;

    @NotNull
    @Column(name = "op_fch_hor_mod", nullable = false)
    private OffsetDateTime oPFchHorMod;

    @Size(max = 40)
    @NotNull
    @Column(name = "op_est_mod", nullable = false, length = 40)
    private String oPEstMod;

}