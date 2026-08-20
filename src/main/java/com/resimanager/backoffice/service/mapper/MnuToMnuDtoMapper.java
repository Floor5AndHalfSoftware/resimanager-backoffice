package com.resimanager.backoffice.service.mapper;

import com.resimanager.backoffice.dto.MenuDto;
import com.resimanager.backoffice.persistance.entity.MenuItem;

import java.util.function.Function;

public class MnuToMnuDtoMapper implements Function<MenuItem, MenuDto> {

    @Override
    public MenuDto apply(MenuItem menuItem) {
        var builder = MenuDto.builder()
            .menuId(menuItem.getId().getMitMenuid())
            .itemId(menuItem.getId().getMItID())
            .nombre(menuItem.getMItNombre())
            .tipo(menuItem.getMItTipo())
            .idPadre(menuItem.getMItItemPadre())
            .orden(menuItem.getMItOrden())
            .idUsrCrea(menuItem.getMitUsrCrea().getId())
            .usrCrea(menuItem.getMitUsrCrea().getPerUsuario())
            .fechaHoraCrea(menuItem.getMItFchHorCrea())
            .estCrea(menuItem.getMItEstCrea())
            .idUsrModifica(menuItem.getMitUsrmod().getId())
            .usrModifica(menuItem.getMitUsrmod().getPerUsuario())
            .fechaHoraModifica(menuItem.getMItFchHorMod())
            .estModifica(menuItem.getMItEstMod())
            .estado(menuItem.getMItSts())
            .controlador(menuItem.getMItControlador())
            .metodo(menuItem.getMItMetodo());

        if (menuItem.getModulo() == null) {
            builder.modulo(menuItem.getMItNombre());
            if (menuItem.getMItNombre().equals("Super Admin")) {
                builder.idModulo(1);
            }
            if (menuItem.getMItNombre().equals("Administradora")) {
                builder.idModulo(2);
            }
            if (menuItem.getMItNombre().equals("Conjunto")) {
                builder.idModulo(3);
            }
            if (menuItem.getMItNombre().equals("Propietario")) {
                builder.idModulo(4);
            }
        } else {
            builder.idModulo(menuItem.getModulo().getModId())
                   .modulo(menuItem.getModulo().getModNombre());

            if (menuItem.getAccion() != null) {
                builder.idAccion(menuItem.getAccion().getId())
                       .accion(menuItem.getAccion().getAccNombre());
            }

            if (menuItem.getOpcion() != null) {
                builder.idOpcion(menuItem.getOpcion().getId().getOpcID())
                       .opcion(menuItem.getOpcion().getOpcNombre());
            }
        }

        return builder.build();
    }
}
