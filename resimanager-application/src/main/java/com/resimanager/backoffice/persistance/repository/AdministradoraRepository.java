package com.resimanager.backoffice.persistance.repository;

import com.resimanager.backoffice.domain.model.Administradora;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdministradoraRepository extends JpaRepository<Administradora, Integer> {

    long countByAdmSts(String admSts);

    /**
     * Obtiene administradoras activas por IDs
     */
    @Query("SELECT a FROM Administradora a WHERE a.id IN :ids AND a.admSts = 'A'")
    List<Administradora> findActiveByIds(@Param("ids") List<Integer> ids);

    /**
     * Lista administradoras con filtros opcionales de estatus y búsqueda por nombre/documento
     */
    @Query("""
            SELECT a FROM Administradora a
            WHERE (:estatus IS NULL OR a.admSts = :estatus)
              AND (:search IS NULL
                   OR LOWER(a.admNombre) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(a.admDocIdent) LIKE LOWER(CONCAT('%', :search, '%')))
            ORDER BY a.admNombre
            """)
    Page<Administradora> findAllWithFilters(@Param("estatus") String estatus,
                                            @Param("search") String search,
                                            Pageable pageable);
}
