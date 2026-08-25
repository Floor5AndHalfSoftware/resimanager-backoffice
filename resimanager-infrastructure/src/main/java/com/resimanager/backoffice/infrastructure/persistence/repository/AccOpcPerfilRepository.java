package com.resimanager.backoffice.infrastructure.persistence.repository;

import com.resimanager.backoffice.domain.model.AccOpcPerfil;
import com.resimanager.backoffice.domain.model.AccOpcPerfilId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for permission checks
 */
@Repository
public interface AccOpcPerfilRepository extends JpaRepository<AccOpcPerfil, AccOpcPerfilId> {
    
    /**
     * Check if a profile has permission for a specific module/action combination
     * @param perfilId Profile ID
     * @param modNombre Module name
     * @param accNombre Action name
     * @return true if permission exists and is active
     */
    @Query("""
        SELECT COUNT(aop) > 0
        FROM AccOpcPerfil aop
        JOIN Modulo m ON m.modId = aop.id.aopModid
        JOIN Accion a ON a.id = aop.id.aopAccid
        WHERE aop.id.aopPrfid = :perfilId
        AND m.modNombre = :modNombre
        AND a.accNombre = :accNombre
        AND aop.aOPSts = 'A'
        AND m.modSts = 'A'
        AND a.accSts = 'A'
    """)
    boolean hasPermission(
        @Param("perfilId") Integer perfilId,
        @Param("modNombre") String modNombre,
        @Param("accNombre") String accNombre
    );

    /**
     * Get all permissions for a profile grouped by module
     * @param perfilId Profile ID
     * @return List of permissions with module and action information
     */
    @Query("""
        SELECT m.modId as moduloId, m.modNombre as moduloNombre, a.accNombre as accionNombre
        FROM AccOpcPerfil aop
        JOIN Modulo m ON m.modId = aop.id.aopModid
        JOIN Accion a ON a.id = aop.id.aopAccid
        WHERE aop.id.aopPrfid = :perfilId
        AND aop.aOPSts = 'A'
        AND m.modSts = 'A'
        AND a.accSts = 'A'
        ORDER BY m.modNombre, a.accNombre
    """)
    List<Object[]> findPermissionsByPerfilId(@Param("perfilId") Integer perfilId);
}
