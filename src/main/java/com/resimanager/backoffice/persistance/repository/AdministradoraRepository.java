package com.resimanager.backoffice.persistance.repository;

import com.resimanager.backoffice.persistance.entity.Administradora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdministradoraRepository extends JpaRepository<Administradora, Integer> {

    /**
     * Obtiene administradoras activas por IDs
     * @param ids Lista de IDs de administradoras
     * @return Lista de administradoras activas
     */
    @Query("SELECT a FROM Administradora a WHERE a.id IN :ids AND a.admSts = 'A'")
    List<Administradora> findActiveByIds(@Param("ids") List<Integer> ids);
}
