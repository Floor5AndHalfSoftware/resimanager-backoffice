package com.resimanager.backoffice.persistance.repository;

import com.resimanager.backoffice.persistance.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Integer> {
    
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
}
