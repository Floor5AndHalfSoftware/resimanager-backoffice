package com.resimanager.backoffice.service.mapper;

import com.resimanager.backoffice.dto.PerfilDTO;
import com.resimanager.backoffice.persistance.entity.Perfil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PerfilMapper {

    @Mapping(source = "prfNombre", target = "nombre")
    @Mapping(source = "prfDescrip", target = "descripcion")
    @Mapping(source = "prfSts", target = "estatus")
    @Mapping(source = "prfNivel", target = "nivel")
    PerfilDTO toDTO(Perfil perfil);
}
