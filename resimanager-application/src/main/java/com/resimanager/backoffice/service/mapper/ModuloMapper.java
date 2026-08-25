package com.resimanager.backoffice.service.mapper;

import com.resimanager.backoffice.dto.ModuloDTO;
import com.resimanager.backoffice.domain.model.Modulo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ModuloMapper {

    @Mapping(source = "modId", target = "id")
    @Mapping(source = "modNombre", target = "nombre")
    @Mapping(source = "modDescrip", target = "descripcion")
    @Mapping(source = "modNivel", target = "nivel")
    ModuloDTO toDTO(Modulo modulo);
}
