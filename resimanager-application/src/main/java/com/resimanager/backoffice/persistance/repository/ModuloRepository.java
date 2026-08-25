package com.resimanager.backoffice.persistance.repository;

import com.resimanager.backoffice.persistance.entity.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Integer> {
    
    /**
     * Find all active modules
     * @return List of active modules
     */
    List<Modulo> findByModSts(String estatus);
    
    /**
     * Find modules by hierarchical level
     * @param nivel Hierarchical level (0-3)
     * @return List of modules
     */
    List<Modulo> findByModNivel(Integer nivel);
    
    /**
     * Find modules by level and status
     * @param nivel Hierarchical level
     * @param estatus Status
     * @return List of modules
     */
    List<Modulo> findByModNivelAndModSts(Integer nivel, String estatus);
    
    /**
     * Find modules assigned to a profile
     * @param perfilId Profile ID
     * @return List of modules
     */
    @Query("""
        SELECT m FROM Modulo m
        JOIN ModPerfil mp ON mp.mpModid.modId = m.modId
        WHERE mp.mpPrfid.id = :perfilId
        AND mp.mPSts = 'A'
        AND m.modSts = 'A'
        ORDER BY m.modNombre ASC
    """)
    List<Modulo> findByPerfilId(@Param("perfilId") Integer perfilId);
    
    /**
     * Find modules by IDs
     * @param ids List of module IDs
     * @return List of modules
     */
    List<Modulo> findByModIdIn(List<Integer> ids);
    
    /**
     * Find active modules by IDs
     * @param ids List of module IDs
     * @return List of active modules
     */
    @Query("SELECT m FROM Modulo m WHERE m.modId IN :ids AND m.modSts = 'A'")
    List<Modulo> findActiveByIds(@Param("ids") List<Integer> ids);
}
