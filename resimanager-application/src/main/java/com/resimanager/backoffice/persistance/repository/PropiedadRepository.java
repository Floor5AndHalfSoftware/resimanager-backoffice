package com.resimanager.backoffice.persistance.repository;

import com.resimanager.backoffice.domain.model.Propiedad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PropiedadRepository extends JpaRepository<Propiedad, Integer> {

    long countByPpSts(String ppSts);

    long countByPpStsAndPpConjId(String ppSts, Integer ppConjId);

    @Query("""
            SELECT p FROM Propiedad p
            WHERE (:estatus IS NULL OR p.ppSts = :estatus)
              AND (:conjuntoId IS NULL OR p.ppConjId = :conjuntoId)
              AND (:search IS NULL OR LOWER(p.ppNumero) LIKE LOWER(CONCAT('%', :search, '%')))
            ORDER BY p.ppConjId, p.ppNumero
            """)
    Page<Propiedad> findAllWithFilters(@Param("estatus") String estatus,
                                       @Param("conjuntoId") Integer conjuntoId,
                                       @Param("search") String search,
                                       Pageable pageable);

    Optional<Propiedad> findByPpidAndPpConjId(Integer ppid, Integer conjId);
}
