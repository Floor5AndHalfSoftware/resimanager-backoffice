package com.resimanager.backoffice.infrastructure.adapter;

import com.resimanager.backoffice.domain.model.ClaseDePropiedadOpcion;
import com.resimanager.backoffice.domain.port.out.ClaseDePropiedadRepositoryPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JpaClaseDePropiedadAdapter implements ClaseDePropiedadRepositoryPort {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<ClaseDePropiedadOpcion> listarActivas() {
        List<Object[]> rows = em.createNativeQuery("""
                SELECT cdpid, cdp_nombre FROM "ClaseDePropiedad"
                WHERE cdp_sts = 'A' ORDER BY cdp_nombre
                """).getResultList();
        return rows.stream()
                .map(r -> new ClaseDePropiedadOpcion((Integer) r[0], (String) r[1]))
                .toList();
    }
}