package com.resimanager.backoffice.service.mapper;

import com.resimanager.backoffice.dto.MenuItemDTO;
import com.resimanager.backoffice.domain.model.MenuItem;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

import java.util.Collections;

@Mapper(componentModel = "spring")
public interface MenuItemMapper {

    @Named("menuItemId")
    default Integer menuItemId(com.resimanager.backoffice.domain.model.MenuItemId id) {
        if (id == null) return null;
        return id.getMItID();
    }

    @Named("moduloNombre")
    default String moduloNombre(com.resimanager.backoffice.domain.model.Modulo modulo) {
        if (modulo == null) return null;
        return modulo.getModNombre();
    }

    @Named("accionNombre")
    default String accionNombre(com.resimanager.backoffice.domain.model.Accion accion) {
        if (accion == null) return null;
        return accion.getAccNombre();
    }

    default MenuItemDTO toDTO(MenuItem menuItem) {
        if (menuItem == null) return null;
        return MenuItemDTO.builder()
                .id(menuItemId(menuItem.getId()))
                .nombre(menuItem.getMItNombre())
                .tipo(menuItem.getMItTipo())
                .itemPadre(menuItem.getMItItemPadre())
                .orden(menuItem.getMItOrden())
                .controlador(menuItem.getMItControlador())
                .metodo(menuItem.getMItMetodo())
                .modulo(moduloNombre(menuItem.getModulo()))
                .accion(accionNombre(menuItem.getAccion()))
                .hijos(Collections.emptyList())
                .build();
    }
}
