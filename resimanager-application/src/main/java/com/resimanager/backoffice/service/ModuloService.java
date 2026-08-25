package com.resimanager.backoffice.service;

import com.resimanager.backoffice.dto.ModuloDTO;
import com.resimanager.backoffice.domain.model.Modulo;
import com.resimanager.backoffice.persistance.repository.ModuloRepository;
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

    private final ModuloRepository moduloRepository;
    private final ModuloMapper moduloMapper;

    public List<ModuloDTO> getModulos(Integer nivel) {
        List<Modulo> modulos;

        if (nivel != null) {
            modulos = moduloRepository.findByModNivelAndModSts(nivel, "A");
            log.debug("Encontrados {} módulos activos de nivel {}", modulos.size(), nivel);
        } else {
            modulos = moduloRepository.findByModSts("A");
            log.debug("Encontrados {} módulos activos", modulos.size());
        }

        return modulos.stream()
                .map(moduloMapper::toDTO)
                .sorted(Comparator.comparing(ModuloDTO::nombre))
                .toList();
    }
}
