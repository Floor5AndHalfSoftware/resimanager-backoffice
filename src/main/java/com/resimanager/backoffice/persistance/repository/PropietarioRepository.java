package com.resimanager.backoffice.persistance.repository;

import com.resimanager.backoffice.persistance.entity.Propietario;
import com.resimanager.backoffice.persistance.entity.PropietarioId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PropietarioRepository extends JpaRepository<Propietario, PropietarioId> {

    @Query("""
            SELECT p FROM Propietario p
            WHERE (:estatus IS NULL OR p.pptSts = :estatus)
              AND (:conjuntoId IS NULL OR p.id.pptConjid = :conjuntoId)
            ORDER BY p.id.pptConjid, p.id.pptPerid
            """)
    Page<Propietario> findAllWithFilters(@Param("estatus") String estatus,
                                         @Param("conjuntoId") Integer conjuntoId,
                                         Pageable pageable);
}
