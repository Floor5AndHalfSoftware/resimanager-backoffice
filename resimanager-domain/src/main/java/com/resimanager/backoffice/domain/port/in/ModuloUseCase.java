package com.resimanager.backoffice.domain.port.in;

import com.resimanager.backoffice.domain.model.Modulo;

import java.util.List;

/**
 * Casos de uso de consulta de módulos del sistema.
 */
public interface ModuloUseCase {

    List<Modulo> obtenerModulos(Integer nivel);
}