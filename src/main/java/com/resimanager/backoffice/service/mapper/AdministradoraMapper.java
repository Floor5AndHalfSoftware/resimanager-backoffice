package com.resimanager.backoffice.service.mapper;

import com.resimanager.backoffice.dto.AdministradoraDTO;
import com.resimanager.backoffice.persistance.entity.Administradora;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdministradoraMapper {

    @Mapping(source = "admNombre", target = "nombre")
    @Mapping(source = "admTelefono", target = "telefono")
    @Mapping(source = "admEMail", target = "email")
    @Mapping(source = "admSts", target = "estatus")
    AdministradoraDTO toDTO(Administradora administradora);
}
