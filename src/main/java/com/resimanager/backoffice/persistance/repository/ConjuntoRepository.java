package com.resimanager.backoffice.persistance.repository;

import com.resimanager.backoffice.persistance.entity.Conjunto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConjuntoRepository extends JpaRepository<Conjunto, Integer> {

    /**
     * Obtiene conjuntos activos por IDs
     * @param ids Lista de IDs de conjuntos
     * @return Lista de conjuntos activos
     */
    @Query("SELECT c FROM Conjunto c WHERE c.id IN :ids AND c.conjSts = 'A'")
    List<Conjunto> findActiveByIds(@Param("ids") List<Integer> ids);
}
