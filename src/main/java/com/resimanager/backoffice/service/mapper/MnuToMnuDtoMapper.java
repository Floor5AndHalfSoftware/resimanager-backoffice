package com.resimanager.backoffice.service.mapper;

import com.resimanager.backoffice.dto.MenuDto;
import com.resimanager.backoffice.persistance.entity.MenuItem;

import java.util.function.Function;

public class MnuToMnuDtoMapper implements Function<MenuItem, MenuDto> {

    @Override
    public MenuDto apply(MenuItem menuItem) {
        MenuDto menuDto = new MenuDto();
        menuDto.setMenuId(menuItem.getId().getMitMenuid());
        menuDto.setItemId(menuItem.getId().getMItID());
        menuDto.setNombre(menuItem.getMItNombre());
        menuDto.setTipo(menuItem.getMItTipo());
        menuDto.setIdPadre(menuItem.getMItItemPadre());
        menuDto.setOrden(menuItem.getMItOrden());
        menuDto.setIdUsrCrea(menuItem.getMitUsrCrea().getId());
        menuDto.setUsrCrea(menuItem.getMitUsrCrea().getPerUsuario());
        menuDto.setFechaHoraCrea(menuItem.getMItFchHorCrea());
        menuDto.setEstCrea(menuItem.getMItEstCrea());
        menuDto.setIdUsrModifica(menuItem.getMitUsrmod().getId());
        menuDto.setUsrModifica(menuItem.getMitUsrmod().getPerUsuario());
        menuDto.setFechaHoraModifica(menuItem.getMItFchHorMod());
        menuDto.setEstModifica(menuItem.getMItEstMod());
        menuDto.setEstado(menuItem.getMItSts());
        menuDto.setControlador(menuItem.getMItControlador());
        menuDto.setMetodo(menuItem.getMItMetodo());
        if (menuItem.getModulo() == null) {
            menuDto.setModulo(menuItem.getMItNombre());
            if (menuItem.getMItNombre().equals("Super Admin")) {
                menuDto.setIdModulo(1);
            }
            if (menuItem.getMItNombre().equals("Administradora")) {
                menuDto.setIdModulo(2);

            }
            if (menuItem.getMItNombre().equals("Conjunto")) {
                menuDto.setIdModulo(3);

            }
            if (menuItem.getMItNombre().equals("Propietario")) {
                menuDto.setIdModulo(4);

            }
        } else {
            menuDto.setIdModulo(menuItem.getModulo().getModId());
            menuDto.setModulo(menuItem.getModulo().getModNombre());
            
            // Accion puede ser null (para separadores o agrupadores)
            if (menuItem.getAccion() != null) {
                menuDto.setIdAccion(menuItem.getAccion().getId());
                menuDto.setAccion(menuItem.getAccion().getAccNombre());
            }
            
            // Opcion puede ser null (para separadores o agrupadores)
            if (menuItem.getOpcion() != null) {
                menuDto.setIdOpcion(menuItem.getOpcion().getId().getOpcID());
                menuDto.setOpcion(menuItem.getOpcion().getOpcNombre());
            }
        }


        return menuDto;
    }
}
