package com.resimanager.backoffice.persistance.repository;

import com.resimanager.backoffice.domain.model.PersAdministradora;
import com.resimanager.backoffice.domain.model.PersAdministradoraId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersAdministradoraRepository extends JpaRepository<PersAdministradora, PersAdministradoraId> {

    /**
     * Obtiene todas las administradoras activas de una persona
     * @param personaId ID de la persona
     * @return Lista de relaciones persona-administradora activas
     */
    @Query("""
        SELECT pa
        FROM PersAdministradora pa
        WHERE pa.id.paPerid = :personaId
          AND pa.pASts = 'A'
        ORDER BY pa.id.paAdmid
    """)
    List<PersAdministradora> findActiveByPersonaId(@Param("personaId") Integer personaId);

    /**
     * Obtiene todas las personas activas de una administradora
     * @param admId ID de la administradora
     * @return Lista de relaciones persona-administradora activas
     */
    @Query("SELECT pa FROM PersAdministradora pa WHERE pa.id.paAdmid = :admId AND pa.pASts = 'A' ORDER BY pa.id.paPerid")
    List<PersAdministradora> findActiveByAdmId(@Param("admId") Integer admId);
}
