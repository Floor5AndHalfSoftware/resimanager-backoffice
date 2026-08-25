package com.resimanager.backoffice.domain.port.in;

import com.resimanager.backoffice.domain.model.ContextoActivo;
import com.resimanager.backoffice.domain.model.ContextoDisponible;
import com.resimanager.backoffice.domain.model.enums.TipoContexto;

import java.util.List;
import java.util.Set;

/**
 * Casos de uso del contexto de trabajo del usuario.
 */
public interface ContextoUseCase {

    List<ContextoDisponible> obtenerContextosDisponibles(Integer personaId);

    Set<String> obtenerRolesDesdePerfiles(Integer personaId);

    ContextoActivo validarYConstruirContexto(Integer personaId, TipoContexto tipo,
                                             Integer entidadId, Integer perfilId);
}