package com.resimanager.backoffice.persistance.repository;

import com.resimanager.backoffice.persistance.entity.PerfPersAdministradora;
import com.resimanager.backoffice.persistance.entity.PerfPersAdministradoraId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerfPersAdministradoraRepository extends JpaRepository<PerfPersAdministradora, PerfPersAdministradoraId> {

    /**
     * Obtiene todos los perfiles activos de una persona en administradoras
     * @param personaId ID de la persona
     * @return Lista de relaciones perfil-persona-administradora activas
     */
    @Query("""
        SELECT ppa
        FROM PerfPersAdministradora ppa
        JOIN FETCH ppa.ppaPrfid perfil
        WHERE ppa.id.ppaPerid = :personaId
          AND ppa.pPASts = 'A'
          AND perfil.prfSts = 'A'
        ORDER BY ppa.id.ppaAdmid, perfil.prfNombre
    """)
    List<PerfPersAdministradora> findActiveByPersonaId(@Param("personaId") Integer personaId);
}
