package com.resimanager.backoffice.persistance.repository;

import com.resimanager.backoffice.domain.model.Conjunto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConjuntoRepository extends JpaRepository<Conjunto, Integer> {

    long countByConjSts(String conjSts);

    /**
     * Obtiene conjuntos activos por IDs
     */
    @Query("SELECT c FROM Conjunto c WHERE c.id IN :ids AND c.conjSts = 'A'")
    List<Conjunto> findActiveByIds(@Param("ids") List<Integer> ids);

    /**
     * Lista conjuntos con filtros opcionales de estatus y búsqueda por nombre/documento
     */
    @Query("""
            SELECT c FROM Conjunto c
            WHERE (:estatus IS NULL OR c.conjSts = :estatus)
              AND (:search IS NULL
                   OR LOWER(c.conjNombre) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(c.conjDocIdent) LIKE LOWER(CONCAT('%', :search, '%')))
            ORDER BY c.conjNombre
            """)
    Page<Conjunto> findAllWithFilters(@Param("estatus") String estatus,
                                      @Param("search") String search,
                                      Pageable pageable);
}
