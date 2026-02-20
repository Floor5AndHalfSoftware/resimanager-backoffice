package com.resimanager.backoffice.persistance.repository;

import com.resimanager.backoffice.persistance.entity.AccOpcPerfil;
import com.resimanager.backoffice.persistance.entity.AccOpcPerfilId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
