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
     * Uses native SQL because MenuItem has complex composite foreign keys
     */
    @Query(value = """
        SELECT DISTINCT mi.*
        FROM menu_item mi
        INNER JOIN acc_opc_perfil aop ON (
            aop.aop_modid = mi.mit_modid AND 
            aop.aop_opcid = mi.mit_opcionid AND 
            aop.aop_accid = mi.mit_accionid AND 
            aop.aop_sts = 'A' AND 
            aop.aop_prfid = :perfilId
        )
        INNER JOIN opc_perfil op ON (
            op.op_modid = mi.mit_modid AND
            op.op_opcid = mi.mit_opcionid AND
            op.op_sts = 'A' AND
            op.op_prfid = :perfilId
        )
        INNER JOIN mod_perfil mp ON ( 
            mp.mp_modid = mi.mit_modid AND 
            mp.mp_sts = 'A' AND
            mp.mp_prfid = :perfilId
        )
        WHERE mi.mit_tipo = 'O' 
        AND mi.mit_sts = 'A' 
        AND mi.mit_menuid = 1
        ORDER BY mi.mit_orden
    """, nativeQuery = true)
    List<MenuItem> findMenusByPerfil(@Param("perfilId") Integer perfilId);


}
