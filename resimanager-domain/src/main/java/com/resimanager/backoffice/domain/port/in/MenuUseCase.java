package com.resimanager.backoffice.domain.port.in;

import com.resimanager.backoffice.domain.model.MenuItem;

import java.util.List;

/**
 * Casos de uso de construcción del menú de navegación.
 */
public interface MenuUseCase {

    List<MenuItem> obtenerMenu();

    List<MenuItem> obtenerMenuPorPerfil(Integer perfilId);

    List<MenuItem> obtenerSubMenus(Integer menuId);
}