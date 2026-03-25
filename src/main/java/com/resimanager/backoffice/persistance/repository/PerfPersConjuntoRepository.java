package com.resimanager.backoffice.persistance.repository;

import com.resimanager.backoffice.persistance.entity.PerfPersConjunto;
import com.resimanager.backoffice.persistance.entity.PerfPersConjuntoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerfPersConjuntoRepository extends JpaRepository<PerfPersConjunto, PerfPersConjuntoId> {

    /**
     * Obtiene todos los perfiles activos de una persona en conjuntos
     * @param personaId ID de la persona
     * @return Lista de relaciones perfil-persona-conjunto activas
     */
    @Query("""
        SELECT ppc
        FROM PerfPersConjunto ppc
        JOIN FETCH ppc.ppcPrfid perfil
        WHERE ppc.id.ppcPerid = :personaId
          AND ppc.pPCSts = 'A'
          AND perfil.prfSts = 'A'
        ORDER BY ppc.id.ppcConjid, perfil.prfNombre
    """)
    List<PerfPersConjunto> findActiveByPersonaId(@Param("personaId") Integer personaId);

    /**
     * Obtiene los perfiles activos de una persona en un conjunto específico
     * @param conjId ID del conjunto
     * @param perId ID de la persona
     * @return Lista de relaciones perfil-persona-conjunto activas
     */
    @Query("SELECT ppc FROM PerfPersConjunto ppc JOIN FETCH ppc.ppcPrfid WHERE ppc.id.ppcConjid = :conjId AND ppc.id.ppcPerid = :perId AND ppc.pPCSts = 'A'")
    List<PerfPersConjunto> findActiveByConjIdAndPerId(@Param("conjId") Integer conjId, @Param("perId") Integer perId);
}
