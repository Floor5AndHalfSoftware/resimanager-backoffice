package com.resimanager.backoffice.dto;

import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.List;

@Builder(toBuilder = true)
public record MenuDto(
    Integer menuId,
    Integer itemId,
    String nombre,
    String tipo,
    Integer idPadre,
    Integer orden,
    Integer idUsrCrea,
    String usrCrea,
    OffsetDateTime fechaHoraCrea,
    String estCrea,
    Integer idUsrModifica,
    String usrModifica,
    OffsetDateTime fechaHoraModifica,
    String estModifica,
    String estado,
    Integer idAccion,
    String accion,
    Integer idOpcion,
    String opcion,
    Integer idModulo,
    String modulo,
    String controlador,
    String metodo,
    List<MenuDto> submenus
) {}
