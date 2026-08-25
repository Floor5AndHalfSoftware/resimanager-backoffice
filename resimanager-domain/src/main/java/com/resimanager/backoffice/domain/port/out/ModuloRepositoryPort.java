package com.resimanager.backoffice.domain.port.out;

import com.resimanager.backoffice.domain.model.Modulo;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida de consulta de módulos.
 */
public interface ModuloRepositoryPort {

    List<Modulo> listarPorNivel(Integer nivel);

    List<Modulo> buscarActivosPorIds(List<Integer> ids);

    List<Modulo> listarPorPerfilId(Integer perfilId);

    Optional<Modulo> buscarPorId(Integer id);

    boolean existePorId(Integer id);
}