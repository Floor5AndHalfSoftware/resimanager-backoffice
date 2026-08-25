package com.resimanager.backoffice.service;

import com.resimanager.backoffice.dto.PropiedadDTO;
import com.resimanager.backoffice.domain.model.Conjunto;
import com.resimanager.backoffice.domain.model.Persona;
import com.resimanager.backoffice.domain.model.Propiedad;
import com.resimanager.backoffice.persistance.repository.ConjuntoRepository;
import com.resimanager.backoffice.persistance.repository.PersonaRepository;
import com.resimanager.backoffice.persistance.repository.PropiedadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropiedadService {

    private final PropiedadRepository propiedadRepository;
    private final ConjuntoRepository conjuntoRepository;
    private final PersonaRepository personaRepository;

    public PropiedadDTO.ListResponse getPropiedades(String estatus, Integer conjuntoId, String search, Integer page, Integer limit) {
        String estatusParam = (estatus != null && !estatus.isBlank()) ? estatus.trim() : null;

        PageRequest pageable = PageRequest.of(page - 1, limit);
        Page<Propiedad> result = propiedadRepository.findAllWithFilters(estatusParam, conjuntoId, search, pageable);

        return new PropiedadDTO.ListResponse(
                result.getContent().stream().map(this::toDTO).toList(),
                result.getTotalElements(),
                page,
                limit
        );
    }

    public PropiedadDTO getPropiedadById(Integer id) {
        Propiedad prop = propiedadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Propiedad no encontrada"));
        return toDTO(prop);
    }

    @Transactional
    public PropiedadDTO createPropiedad(Integer conjId, Integer cdpId, String numero,
                                         BigDecimal cantidad, BigDecimal coefParticipacion,
                                         String username, String estacion) {
        Propiedad prop = new Propiedad();
        prop.setPpConjId(conjId);
        prop.setPpCdpId(cdpId);
        prop.setPpNumero(numero);
        prop.setPpCantidad(cantidad);
        prop.setPpCoefParticipacion(coefParticipacion);
        prop.setPpSts("A");
        prop.setPpUsrCrea(findPersonaIdByUsuario(username));
        prop.setPpFchHorCrea(OffsetDateTime.now());
        prop.setPpEstCrea(estacion);
        prop.setPpUsrMod(findPersonaIdByUsuario(username));
        prop.setPpFchHorMod(OffsetDateTime.now());
        prop.setPpEstMod(estacion);

        return toDTO(propiedadRepository.save(prop));
    }

    @Transactional
    public PropiedadDTO updatePropiedad(Integer id, Integer cdpId, String numero,
                                         BigDecimal cantidad, BigDecimal coefParticipacion,
                                         String estatus, String username, String estacion) {
        Propiedad prop = propiedadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Propiedad no encontrada"));

        if (cdpId != null) prop.setPpCdpId(cdpId);
        if (numero != null) prop.setPpNumero(numero);
        if (cantidad != null) prop.setPpCantidad(cantidad);
        if (coefParticipacion != null) prop.setPpCoefParticipacion(coefParticipacion);
        if (estatus != null && (estatus.equals("A") || estatus.equals("I"))) prop.setPpSts(estatus);

        prop.setPpUsrMod(findPersonaIdByUsuario(username));
        prop.setPpFchHorMod(OffsetDateTime.now());
        prop.setPpEstMod(estacion);

        return toDTO(propiedadRepository.save(prop));
    }

    @Transactional
    public Map<String, String> deletePropiedad(Integer id, String username, String estacion) {
        Propiedad prop = propiedadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Propiedad no encontrada"));

        prop.setPpSts("I");
        prop.setPpUsrMod(findPersonaIdByUsuario(username));
        prop.setPpFchHorMod(OffsetDateTime.now());
        prop.setPpEstMod(estacion);

        propiedadRepository.save(prop);
        return Map.of("message", "Propiedad inactivada correctamente");
    }

    private PropiedadDTO toDTO(Propiedad p) {
        String conjNombre = null;
        Optional<Conjunto> conj = conjuntoRepository.findById(p.getPpConjId());
        if (conj.isPresent()) {
            conjNombre = conj.get().getConjNombre();
        }

        return PropiedadDTO.builder()
                .ppid(p.getPpid())
                .ppConjId(p.getPpConjId())
                .conjuntoNombre(conjNombre)
                .ppCdpId(p.getPpCdpId())
                .ppNumero(p.getPpNumero())
                .ppCantidad(p.getPpCantidad())
                .ppCoefParticipacion(p.getPpCoefParticipacion())
                .estatus(p.getPpSts())
                .build();
    }

    private Integer findPersonaIdByUsuario(String username) {
        return personaRepository.findByPerUsuario(username)
                .map(Persona::getId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado: " + username));
    }
}
