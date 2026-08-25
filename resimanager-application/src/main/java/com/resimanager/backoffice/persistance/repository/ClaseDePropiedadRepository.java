package com.resimanager.backoffice.persistance.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class ClaseDePropiedadRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Map<String, Object>> findActivas() {
        List<Object[]> rows = em.createNativeQuery("""
                SELECT cdpid, cdp_nombre FROM "ClaseDePropiedad"
                WHERE cdp_sts = 'A' ORDER BY cdp_nombre
                """).getResultList();
        return rows.stream().map(r -> Map.of("id", r[0], "nombre", r[1])).toList();
    }
}
