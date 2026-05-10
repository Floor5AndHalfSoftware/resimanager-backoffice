package com.resimanager.backoffice.service;

import com.resimanager.backoffice.dto.ModuloDTO;
import com.resimanager.backoffice.persistance.entity.Modulo;
import com.resimanager.backoffice.persistance.repository.ModuloRepository;
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
                .map(this::toDTO)
                .sorted(Comparator.comparing(ModuloDTO::getNombre))
                .toList();
    }

    private ModuloDTO toDTO(Modulo modulo) {
        return ModuloDTO.builder()
                .id(modulo.getModId())
                .nombre(modulo.getModNombre())
                .descripcion(modulo.getModDescrip())
                .nivel(modulo.getModNivel())
                .build();
    }
}
