package com.resimanager.backoffice.persistance.repository;

import com.resimanager.backoffice.persistance.entity.PersConjunto;
import com.resimanager.backoffice.persistance.entity.PersConjuntoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersConjuntoRepository extends JpaRepository<PersConjunto, PersConjuntoId> {

    /**
     * Obtiene todos los conjuntos activos de una persona
     * @param personaId ID de la persona
     * @return Lista de relaciones persona-conjunto activas
     */
    @Query("""
        SELECT pc
        FROM PersConjunto pc
        WHERE pc.id.pcPerid = :personaId
          AND pc.pCSts = 'A'
        ORDER BY pc.id.pcConjid
    """)
    List<PersConjunto> findActiveByPersonaId(@Param("personaId") Integer personaId);

    /**
     * Obtiene todas las personas activas de un conjunto
     * @param conjId ID del conjunto
     * @return Lista de relaciones persona-conjunto activas
     */
    @Query("SELECT pc FROM PersConjunto pc WHERE pc.id.pcConjid = :conjId AND pc.pCSts = 'A' ORDER BY pc.id.pcPerid")
    List<PersConjunto> findActiveByConjId(@Param("conjId") Integer conjId);
}
