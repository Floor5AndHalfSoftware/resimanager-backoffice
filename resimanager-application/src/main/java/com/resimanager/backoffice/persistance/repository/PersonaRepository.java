package com.resimanager.backoffice.persistance.repository;

import com.resimanager.backoffice.domain.model.Persona;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Integer> {

    long countByPerSts(String perSts);
    
    /**
     * Find user by username (per_usuario field)
     * @param username Username to search
     * @return Optional containing Persona if found
     */
    Optional<Persona> findByPerUsuario(String username);
    
    /**
     * Find user by email (per_email field)
     * @param email Email to search
     * @return Optional containing Persona if found
     */
    Optional<Persona> findByPerEMail(String email);
    
    /**
     * Find user by username OR email
     * Useful for login where user can use either
     * @param username Username to search
     * @param email Email to search
     * @return Optional containing Persona if found
     */
    Optional<Persona> findByPerUsuarioOrPerEMail(String username, String email);
    
    /**
     * Check if username exists
     * @param username Username to check
     * @return true if exists
     */
    boolean existsByPerUsuario(String username);
    
    /**
     * Check if email exists
     * @param email Email to check
     * @return true if exists
     */
    boolean existsByPerEMail(String email);

    @Query("""
            SELECT p FROM Persona p
            WHERE (:estatus IS NULL OR p.perSts = :estatus)
            AND (:search IS NULL OR
                 LOWER(p.perNombre) LIKE LOWER(CONCAT('%', :search, '%')) OR
                 LOWER(p.perApellido) LIKE LOWER(CONCAT('%', :search, '%')) OR
                 LOWER(p.perDocIdent) LIKE LOWER(CONCAT('%', :search, '%')) OR
                 LOWER(p.perEMail) LIKE LOWER(CONCAT('%', :search, '%')))
            ORDER BY p.perNombre, p.perApellido
            """)
    Page<Persona> findAllWithFilters(
            @Param("estatus") String estatus,
            @Param("search") String search,
            Pageable pageable
    );
}
