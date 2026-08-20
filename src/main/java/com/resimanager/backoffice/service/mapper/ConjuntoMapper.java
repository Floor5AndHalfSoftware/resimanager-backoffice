package com.resimanager.backoffice.service.mapper;

import com.resimanager.backoffice.dto.ConjuntoDTO;
import com.resimanager.backoffice.persistance.entity.Conjunto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConjuntoMapper {

    @Mapping(source = "conjNombre", target = "nombre")
    @Mapping(source = "conjTelefono", target = "telefono")
    @Mapping(source = "conjEMail", target = "email")
    @Mapping(source = "conjSts", target = "estatus")
    @Mapping(source = "conjPersContacto.id", target = "persContactoId")
    ConjuntoDTO toDTO(Conjunto conjunto);
}
