package com.resimanager.backoffice.service.mapper;

import com.resimanager.backoffice.dto.UserInfoDTO;
import com.resimanager.backoffice.domain.model.Persona;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PersonaMapper {

    @Mapping(source = "perUsuario", target = "usuario")
    @Mapping(source = "perNombre", target = "nombre")
    @Mapping(source = "perApellido", target = "apellido")
    @Mapping(source = "perEMail", target = "email")
    @Mapping(source = "perDocIdent", target = "documento")
    UserInfoDTO toUserInfoDTO(Persona persona);
}
