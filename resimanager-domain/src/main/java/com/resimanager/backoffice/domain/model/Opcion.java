package com.resimanager.backoffice.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "opcion")
public class Opcion {
    @EmbeddedId
    private OpcionId id;

    @Size(max = 80)
    @NotNull
    @Column(name = "opc_nombre", nullable = false, length = 80)
    private String opcNombre;

    @Size(max = 120)
    @NotNull
    @Column(name = "opc_descrip", nullable = false, length = 120)
    private String opcDescrip;

    @Size(max = 1)
    @NotNull
    @Column(name = "opc_sts", nullable = false, length = 1)
    private String opcSts;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "opc_usr_crea", nullable = false)
    private Persona opcUsrcrea;

    @NotNull
    @Column(name = "opc_fch_hor_crea", nullable = false)
    private OffsetDateTime opcFchHorCrea;

    @Size(max = 40)
    @NotNull
    @Column(name = "opc_est_crea", nullable = false, length = 40)
    private String opcEstCrea;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "opc_usr_mod", nullable = false)
    private Persona opcUsrmod;

    @NotNull
    @Column(name = "opc_fch_hor_mod", nullable = false)
    private OffsetDateTime opcFchHorMod;

    @Size(max = 40)
    @NotNull
    @Column(name = "opc_est_mod", nullable = false, length = 40)
    private String opcEstMod;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "opcion")
    private List<MenuItem> menuItems;


}