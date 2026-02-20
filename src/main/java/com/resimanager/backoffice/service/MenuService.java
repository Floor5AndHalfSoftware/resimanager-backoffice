package com.resimanager.backoffice.service;

import com.resimanager.backoffice.dto.MenuDto;
import com.resimanager.backoffice.persistance.entity.MenuItem;
import com.resimanager.backoffice.persistance.entity.MenuItemId;
import com.resimanager.backoffice.persistance.repository.MenuItemRepository;
import com.resimanager.backoffice.service.mapper.MnuToMnuDtoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MenuService {

    private final MenuItemRepository menuItemRepository;

    public MenuService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    /**
     * Get all menus (unfiltered - for backward compatibility)
     */
    public List<MenuDto> menus(){
        List<MenuItem> menuItems = menuItemRepository.findMenus();
        List<MenuDto> menusDto = new ArrayList<>();

        for (MenuItem menuItem : menuItems) {
            List<MenuDto> menusHijosDto = new ArrayList<>();
            MenuDto menuDto = new MnuToMnuDtoMapper().apply(menuItem);
            List<MenuItem> menusHijos= subMenus(menuItem.getId());
            for (MenuItem menuHijo : menusHijos) {
                MenuDto menuHijoDto =new MnuToMnuDtoMapper().apply(menuHijo);
                menuHijoDto.setSubmenus(new ArrayList<>());
                menusHijosDto.add(menuHijoDto);
            }
            menuDto.setSubmenus(menusHijosDto);
            menusDto.add(menuDto);
        }
        return menusDto;
    }
    
    /**
     * Get menus filtered by user's active profile permissions
     * @param perfilId Active profile ID from JWT context
     * @return Hierarchical menu structure with only accessible items
     */
    public List<MenuDto> menusByPerfil(Integer perfilId) {
        log.debug("Fetching menus for profile ID: {}", perfilId);
        
        // Profile 1 (Super Admin) gets all menus
        if (perfilId == 1) {
            log.debug("Super admin detected, returning all menus");
            return menus();
        }
        
        // Get menu items accessible by this profile
        List<MenuItem> menuItems = menuItemRepository.findMenusByPerfil(perfilId);
        
        log.debug("Found {} menu items for profile {}", menuItems.size(), perfilId);
        
        // Build hierarchical structure
        return buildMenuHierarchy(menuItems);
    }
    
    /**
     * Build hierarchical menu structure from flat list
     */
    private List<MenuDto> buildMenuHierarchy(List<MenuItem> menuItems) {
        Map<Integer, MenuDto> menuMap = new HashMap<>();
        List<MenuDto> rootMenus = new ArrayList<>();
        
        // Convert all items to DTOs
        MnuToMnuDtoMapper mapper = new MnuToMnuDtoMapper();
        for (MenuItem item : menuItems) {
            MenuDto dto = mapper.apply(item);
            dto.setSubmenus(new ArrayList<>());
            menuMap.put(dto.getItemId(), dto);
        }
        
        // Build hierarchy
        for (MenuDto dto : menuMap.values()) {
            if (dto.getIdPadre() == 0) {
                // Root level item
                rootMenus.add(dto);
            } else {
                // Child item - add to parent's submenu
                MenuDto parent = menuMap.get(dto.getIdPadre());
                if (parent != null) {
                    parent.getSubmenus().add(dto);
                } else {
                    // Parent not accessible by this profile, add as root
                    rootMenus.add(dto);
                }
            }
        }
        
        // Sort by order
        rootMenus.sort((a, b) -> Integer.compare(a.getOrden(), b.getOrden()));
        for (MenuDto menu : rootMenus) {
            sortSubmenus(menu);
        }
        
        return rootMenus;
    }
    
    /**
     * Recursively sort submenus
     */
    private void sortSubmenus(MenuDto menu) {
        if (menu.getSubmenus() != null && !menu.getSubmenus().isEmpty()) {
            menu.getSubmenus().sort((a, b) -> Integer.compare(a.getOrden(), b.getOrden()));
            for (MenuDto submenu : menu.getSubmenus()) {
                sortSubmenus(submenu);
            }
        }
    }

    public List<MenuItem> subMenus(MenuItemId menuid){
        return menuItemRepository.findSubMenus(menuid.getMItID());
    }
}
