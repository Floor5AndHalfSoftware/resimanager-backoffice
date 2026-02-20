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
@Table(name = "\"Persona\"")
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "perid", nullable = false)
    private Integer id;

    @Size(max = 20)
    @NotNull
    @Column(name = "per_doc_ident", nullable = false, length = 20)
    private String perDocIdent;

    @Size(max = 80)
    @NotNull
    @Column(name = "per_nombre", nullable = false, length = 80)
    private String perNombre;

    @Size(max = 80)
    @NotNull
    @Column(name = "per_apellido", nullable = false, length = 80)
    private String perApellido;

    @Size(max = 15)
    @NotNull
    @Column(name = "per_tlf_cel", nullable = false, length = 15)
    private String perTlfCel;

    @Size(max = 80)
    @NotNull
    @Column(name = "per_email", nullable = false, length = 80)
    private String perEMail;

    @Size(max = 80)
    @Column(name = "per_usuario", length = 80)
    private String perUsuario;

    @Size(max = 80)
    @Column(name = "per_clave", length = 80)
    private String perClave;

    @Size(max = 1)
    @NotNull
    @Column(name = "per_sts", nullable = false, length = 1)
    private String perSts;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "per_usr_crea", nullable = false)
    private Persona perUsrcrea;

    @NotNull
    @Column(name = "per_fch_hor_crea", nullable = false)
    private OffsetDateTime perFchHorCrea;

    @Size(max = 40)
    @NotNull
    @Column(name = "per_est_crea", nullable = false, length = 40)
    private String perEstCrea;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "per_usr_mod", nullable = false)
    private Persona perUsrmod;

    @NotNull
    @Column(name = "per_fch_hor_mod", nullable = false)
    private OffsetDateTime perFchHorMod;

    @Size(max = 40)
    @NotNull
    @Column(name = "per_est_mod", nullable = false, length = 40)
    private String perEstMod;

}