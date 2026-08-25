package com.resimanager.backoffice.persistance.repository;

import com.resimanager.backoffice.domain.model.Perfil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerfilRepository extends JpaRepository<Perfil, Integer> {
    
    /**
     * Find profile by ID and status
     * @param id Profile ID
     * @param estatus Status ('A' or 'I')
     * @return Optional containing Perfil if found
     */
    Optional<Perfil> findByIdAndPrfSts(Integer id, String estatus);
    
    /**
     * Find all active profiles
     * @return List of active profiles
     */
    List<Perfil> findByPrfSts(String estatus);
    
    /**
     * Find profiles by hierarchical level
     * @param nivel Hierarchical level (0-3)
     * @return List of profiles
     */
    List<Perfil> findByPrfNivel(Integer nivel);
    
    /**
     * Find profiles by level and status
     * @param nivel Hierarchical level
     * @param estatus Status
     * @return List of profiles
     */
    List<Perfil> findByPrfNivelAndPrfSts(Integer nivel, String estatus);
    
    /**
     * Search profiles by name (case insensitive)
     * @param nombre Name to search
     * @param pageable Pagination
     * @return Page of profiles
     */
    @Query("SELECT p FROM Perfil p WHERE LOWER(p.prfNombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    Page<Perfil> searchByNombre(@Param("nombre") String nombre, Pageable pageable);
    
    /**
     * Find profiles with filters
     * @param estatus Status filter (optional)
     * @param nivel Level filter (optional)
     * @param search Search term (optional)
     * @param pageable Pagination
     * @return Page of profiles
     */
    @Query("""
        SELECT p FROM Perfil p 
        WHERE (:estatus IS NULL OR p.prfSts = :estatus)
        AND (:nivel IS NULL OR p.prfNivel = :nivel)
        AND (:search IS NULL OR LOWER(p.prfNombre) LIKE LOWER(CONCAT('%', :search, '%'))
                              OR LOWER(p.prfDescrip) LIKE LOWER(CONCAT('%', :search, '%')))
        ORDER BY p.prfNivel ASC, p.prfNombre ASC
    """)
    Page<Perfil> findWithFilters(
        @Param("estatus") String estatus,
        @Param("nivel") Integer nivel,
        @Param("search") String search,
        Pageable pageable
    );
    
    /**
     * Count assigned users for a profile
     * @param perfilId Profile ID
     * @return Number of users assigned
     */
    @Query("""
        SELECT COUNT(DISTINCT ppa.id.ppaPerid) + COUNT(DISTINCT ppc.id.ppcPerid)
        FROM Perfil p
        LEFT JOIN PerfPersAdministradora ppa ON ppa.id.ppaPrfid = p.id
        LEFT JOIN PerfPersConjunto ppc ON ppc.id.ppcPrfid = p.id
        WHERE p.id = :perfilId
    """)
    Long countUsuariosAsignados(@Param("perfilId") Integer perfilId);
    
    /**
     * Check if profile name already exists
     * @param nombre Profile name
     * @return true if exists
     */
    boolean existsByPrfNombre(String nombre);
    
    /**
     * Check if profile name already exists excluding a specific ID
     * @param nombre Profile name
     * @param id Profile ID to exclude
     * @return true if exists
     */
    boolean existsByPrfNombreAndIdNot(String nombre, Integer id);
}
