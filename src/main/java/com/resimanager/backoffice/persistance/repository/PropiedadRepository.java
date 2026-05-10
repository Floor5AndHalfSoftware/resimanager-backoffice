package com.resimanager.backoffice.persistance.repository;

import com.resimanager.backoffice.persistance.entity.Propiedad;
import com.resimanager.backoffice.persistance.entity.PropiedadId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PropiedadRepository extends JpaRepository<Propiedad, PropiedadId> {

    @Query("SELECT p FROM Propiedad p WHERE p.id.ppdID = :ppdId AND p.id.ppdConjid = :conjId")
    Optional<Propiedad> findByPpdIDAndPpdConjid(@Param("ppdId") Integer ppdId, @Param("conjId") Integer conjId);
}
