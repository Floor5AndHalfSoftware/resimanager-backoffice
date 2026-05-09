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

import java.time.Instant;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "\"Administradora\"")
public class Administradora {
    @Id
    @Column(name = "admid", nullable = false)
    private Integer id;

    @Size(max = 50)
    @Column(name = "adm_doc_ident", length = 50)
    private String admDocIdent;

    @Size(max = 250)
    @Column(name = "adm_nombre", length = 250)
    private String admNombre;

    @Size(max = 15)
    @Column(name = "adm_telefono", length = 15)
    private String admTelefono;

    @Size(max = 250)
    @Column(name = "adm_email", length = 250)
    private String admEMail;

    @Size(max = 1)
    @NotNull
    @Column(name = "adm_sts", nullable = false, length = 1)
    private String admSts;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "adm_pers_contacto", nullable = false)
    private Persona admPersContacto;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "adm_usr_crea", nullable = false)
    private Persona admUsrCrea;

    @NotNull
    @Column(name = "adm_fch_hor_crea", nullable = false)
    private OffsetDateTime admFchHorCrea;

    @Size(max = 40)
    @NotNull
    @Column(name = "adm_est_crea", nullable = false, length = 40)
    private String admEstCrea;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "adm_usr_mod", nullable = false)
    private Persona admUsrMod;

    @NotNull
    @Column(name = "adm_fch_hor_mod", nullable = false)
    private OffsetDateTime admFchHorMod;

    @Size(max = 40)
    @NotNull
    @Column(name = "adm_est_mod", nullable = false, length = 40)
    private String admEstMod;

}