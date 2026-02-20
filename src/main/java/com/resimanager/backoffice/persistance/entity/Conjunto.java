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
@Table(name = "\"Conjunto\"")
public class Conjunto {
    @Id
    @Column(name = "conjid", nullable = false)
    private Integer id;

    @Size(max = 50)
    @Column(name = "conj_doc_ident", length = 50)
    private String conjDocIdent;

    @Size(max = 250)
    @Column(name = "conj_nombre", length = 250)
    private String conjNombre;

    @Size(max = 15)
    @Column(name = "conj_telefono", length = 15)
    private String conjTelefono;

    @Size(max = 250)
    @Column(name = "conj_email", length = 250)
    private String conjEMail;

    @Size(max = 1)
    @NotNull
    @Column(name = "conj_sts", nullable = false, length = 1)
    private String conjSts;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "conj_pers_contacto", nullable = false)
    private Persona conjPersContacto;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "conj_usr_crea", nullable = false)
    private Persona conjUsrCrea;

    @NotNull
    @Column(name = "conj_fch_hor_crea", nullable = false)
    private OffsetDateTime conjFchHorCrea;

    @Size(max = 40)
    @NotNull
    @Column(name = "conj_est_crea", nullable = false, length = 40)
    private String conjEstCrea;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "conj_usr_mod", nullable = false)
    private Persona conjUsrMod;

    @NotNull
    @Column(name = "conj_fch_hor_mod", nullable = false)
    private OffsetDateTime conjFchHorMod;

    @Size(max = 40)
    @NotNull
    @Column(name = "conj_est_mod", nullable = false, length = 40)
    private String conjEstMod;

}