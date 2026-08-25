package com.resimanager.backoffice.persistance.repository;

import com.resimanager.backoffice.persistance.entity.MenuItem;
import com.resimanager.backoffice.persistance.entity.MenuItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, MenuItemId> {
    @Query("Select u from MenuItem u " +
            "where u.mItTipo='A' " +
            "order by u.id.mItID "
    )
    List<MenuItem> findMenus();

    @Query("Select u from MenuItem u " +
            "where u.mItItemPadre=?1 " +
            "order by u.id.mItID "
    )
    List<MenuItem> findSubMenus(Integer menuItemPadre);

    /**
     * Find menu items accessible by a specific profile
     * This query joins with permission tables to ensure user has access
     * Items without module/option/action (Dashboard, groupers) are always visible
     * Uses native SQL because MenuItem has complex composite foreign keys
     */
    @Query(value = """
        SELECT DISTINCT mi.*
        FROM menu_item mi
        WHERE mi.mit_tipo IN ('O', 'A') 
        AND mi.mit_sts = 'A' 
        AND mi.mit_menuid = 1
        AND (
            -- Items without module (Dashboard, groupers) are always visible
            mi.mit_modid IS NULL
            OR
            -- Items with module must have permission
            EXISTS (
                SELECT 1 FROM "ModPerfil" mp 
                WHERE mp.mp_mod_id = mi.mit_modid 
                AND mp.mp_sts = 'A' 
                AND mp.mp_prf_id = :perfilId
            )
        )
        ORDER BY mi.mit_orden
    """, nativeQuery = true)
    List<MenuItem> findMenusByPerfil(@Param("perfilId") Integer perfilId);


}
