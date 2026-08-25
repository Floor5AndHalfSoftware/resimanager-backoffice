package com.resimanager.backoffice.infrastructure.persistence.repository;

import com.resimanager.backoffice.domain.model.ModPerfil;
import com.resimanager.backoffice.domain.model.ModPerfilId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModPerfilRepository extends JpaRepository<ModPerfil, ModPerfilId> {
    
    /**
     * Find all active module-profile assignments for a profile
     * @param perfilId Profile ID
     * @return List of ModPerfil
     */
    @Query("SELECT mp FROM ModPerfil mp WHERE mp.mpPrfid.id = :perfilId AND mp.mPSts = 'A'")
    List<ModPerfil> findActiveByPerfilId(@Param("perfilId") Integer perfilId);
    
    /**
     * Find assignment by profile and module
     * @param perfilId Profile ID
     * @param moduloId Module ID
     * @return ModPerfil if exists
     */
    @Query("SELECT mp FROM ModPerfil mp WHERE mp.mpPrfid.id = :perfilId AND mp.mpModid.modId = :moduloId")
    ModPerfil findByPerfilIdAndModuloId(@Param("perfilId") Integer perfilId, @Param("moduloId") Integer moduloId);
    
    /**
     * Delete (inactivate) module assignment from profile
     * @param perfilId Profile ID
     * @param moduloId Module ID
     */
    @Modifying
    @Query("UPDATE ModPerfil mp SET mp.mPSts = 'I' WHERE mp.mpPrfid.id = :perfilId AND mp.mpModid.modId = :moduloId")
    void deleteByPerfilIdAndModuloId(@Param("perfilId") Integer perfilId, @Param("moduloId") Integer moduloId);
    
    /**
     * Delete all module assignments for a profile
     * @param perfilId Profile ID
     */
    @Modifying
    @Query("UPDATE ModPerfil mp SET mp.mPSts = 'I' WHERE mp.mpPrfid.id = :perfilId")
    void deleteAllByPerfilId(@Param("perfilId") Integer perfilId);
    
    /**
     * Check if module is assigned to profile
     * @param perfilId Profile ID
     * @param moduloId Module ID
     * @return true if assigned
     */
    @Query("SELECT COUNT(mp) > 0 FROM ModPerfil mp WHERE mp.mpPrfid.id = :perfilId AND mp.mpModid.modId = :moduloId AND mp.mPSts = 'A'")
    boolean existsByPerfilIdAndModuloId(@Param("perfilId") Integer perfilId, @Param("moduloId") Integer moduloId);
}
