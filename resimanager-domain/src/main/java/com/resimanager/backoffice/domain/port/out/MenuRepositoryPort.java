package com.resimanager.backoffice.domain.port.out;

import com.resimanager.backoffice.domain.model.MenuItem;

import java.util.List;

/**
 * Puerto de salida de consulta de menú.
 */
public interface MenuRepositoryPort {

    List<MenuItem> listarMenu();

    List<MenuItem> listarMenuPorPerfil(Integer perfilId);

    List<MenuItem> listarSubMenus(Integer menuId);
}