package com.resimanager.backoffice.service;

import com.resimanager.backoffice.domain.model.Modulo;
import com.resimanager.backoffice.domain.port.in.ModuloUseCase;
import com.resimanager.backoffice.domain.port.out.ModuloRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModuloService implements ModuloUseCase {

    private final ModuloRepositoryPort moduloRepositoryPort;

    @Override
    public List<Modulo> obtenerModulos(Integer nivel) {
        return nivel != null
                ? moduloRepositoryPort.listarPorNivel(nivel).stream().filter(m -> "A".equals(m.getModSts())).toList()
                : moduloRepositoryPort.listarPorNivel(null);
    }
}