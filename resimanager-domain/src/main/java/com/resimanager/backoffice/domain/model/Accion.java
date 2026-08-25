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
@Table(name = "accion")
public class Accion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "accid", nullable = false)
    private Integer id;

    @Size(max = 80)
    @NotNull
    @Column(name = "acc_nombre", nullable = false, length = 80)
    private String accNombre;

    @Size(max = 120)
    @NotNull
    @Column(name = "acc_descrip", nullable = false, length = 120)
    private String accDescrip;

    @Size(max = 1)
    @NotNull
    @Column(name = "acc_sts", nullable = false, length = 1)
    private String accSts;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "acc_usr_crea", nullable = false)
    private Persona accUsrcrea;

    @NotNull
    @Column(name = "acc_fch_hor_crea", nullable = false)
    private OffsetDateTime accFchHorCrea;

    @Size(max = 40)
    @NotNull
    @Column(name = "acc_est_crea", nullable = false, length = 40)
    private String accEstCrea;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "acc_usr_mod", nullable = false)
    private Persona accUsrmod;

    @NotNull
    @Column(name = "acc_fch_hor_mod", nullable = false)
    private OffsetDateTime accFchHorMod;

    @Size(max = 40)
    @NotNull
    @Column(name = "acc_est_mod", nullable = false, length = 40)
    private String accEstMod;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "accion")
    private List<MenuItem> menuItems;

}