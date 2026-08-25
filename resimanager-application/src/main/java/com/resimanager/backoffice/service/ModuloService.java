package com.resimanager.backoffice.service;

import com.resimanager.backoffice.dto.ModuloDTO;
import com.resimanager.backoffice.domain.model.Modulo;
import com.resimanager.backoffice.domain.port.out.ModuloRepositoryPort;
import com.resimanager.backoffice.service.mapper.ModuloMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModuloService {

    private final ModuloRepositoryPort moduloRepositoryPort;
    private final ModuloMapper moduloMapper;

    public List<ModuloDTO> getModulos(Integer nivel) {
        List<Modulo> modulos = nivel != null
                ? moduloRepositoryPort.listarPorNivel(nivel).stream().filter(m -> "A".equals(m.getModSts())).toList()
                : moduloRepositoryPort.listarPorNivel(null);

        return modulos.stream()
                .map(moduloMapper::toDTO)
                .sorted(Comparator.comparing(ModuloDTO::nombre))
                .toList();
    }
}