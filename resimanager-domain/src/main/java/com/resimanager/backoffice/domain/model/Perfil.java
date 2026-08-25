package com.resimanager.backoffice.domain.model;

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
@Table(name = "\"Perfil\"")
public class Perfil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prfid", nullable = false)
    private Integer id;

    @Size(max = 80)
    @NotNull
    @Column(name = "prf_nombre", nullable = false, length = 80)
    private String prfNombre;

    @Size(max = 120)
    @Column(name = "prf_descrip", length = 120)
    private String prfDescrip;

    @Size(max = 1)
    @NotNull
    @Column(name = "prf_sts", nullable = false, length = 1)
    private String prfSts;

    @NotNull
    @Column(name = "prf_nivel", nullable = false)
    private Integer prfNivel;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "prf_usr_crea", nullable = false)
    private Persona prfUsrcrea;

    @NotNull
    @Column(name = "prf_fch_hor_crea", nullable = false)
    private OffsetDateTime prfFchHorCrea;

    @Size(max = 40)
    @NotNull
    @Column(name = "prf_est_crea", nullable = false, length = 40)
    private String prfEstCrea;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "prf_usr_mod", nullable = false)
    private Persona prfUsrmod;

    @NotNull
    @Column(name = "prf_fch_hor_mod", nullable = false)
    private OffsetDateTime prfFchHorMod;

    @Size(max = 40)
    @NotNull
    @Column(name = "prf_est_mod", nullable = false, length = 40)
    private String prfEstMod;

}