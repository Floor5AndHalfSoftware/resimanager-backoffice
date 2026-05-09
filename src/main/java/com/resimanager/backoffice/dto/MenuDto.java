package com.resimanager.backoffice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MenuDto {

    private Integer menuId;
    private Integer itemId;
    private String nombre;
    private String tipo;
    private Integer idPadre;
    private Integer orden;
    private Integer idUsrCrea;
    private String usrCrea;
    private OffsetDateTime fechaHoraCrea;
    private String estCrea;
    private Integer idUsrModifica;
    private String usrModifica;
    private OffsetDateTime fechaHoraModifica;
    private String estModifica;
    private String estado;
    private Integer idAccion;
    private String accion;
    private Integer idOpcion;
    private String opcion;
    private Integer idModulo;
    private String modulo;
    private String controlador;
    private String metodo;
    private List<MenuDto> submenus;

}
