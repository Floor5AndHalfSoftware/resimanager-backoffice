package com.resimanager.backoffice.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "\"Menu\"")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menuid", nullable = false)
    private Integer id;

    @Size(max = 80)
    @NotNull
    @Column(name = "menu_nombre", nullable = false, length = 80)
    private String menuNombre;

    @Size(max = 120)
    @NotNull
    @Column(name = "menu_descrip", nullable = false, length = 120)
    private String menuDescrip;

    @Size(max = 1)
    @NotNull
    @Column(name = "menu_sts", nullable = false, length = 1)
    private String menuSts;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "menu_usr_crea", nullable = false)
    private Persona menuUsrcrea;

    @NotNull
    @Column(name = "menu_fch_hor_crea", nullable = false)
    private OffsetDateTime menuFchHorCrea;

    @Size(max = 40)
    @NotNull
    @Column(name = "menu_est_crea", nullable = false, length = 40)
    private String menuEstCrea;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "menu_usr_mod", nullable = false)
    private Persona menuUsrmod;

    @NotNull
    @Column(name = "menu_fch_hor_mod", nullable = false)
    private OffsetDateTime menuFchHorMod;

    @Size(max = 40)
    @NotNull
    @Column(name = "menu_est_mod", nullable = false, length = 40)
    private String menuEstMod;

    @Size(max = 1)
    @NotNull
    @Column(name = "menu_posicion", nullable = false, length = 1)
    private String menuPosicion;

}