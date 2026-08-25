package com.resimanager.backoffice.service;

import com.resimanager.backoffice.dto.PropietarioDTO;
import com.resimanager.backoffice.dto.PropietarioListResponse;
import com.resimanager.backoffice.domain.model.Persona;
import com.resimanager.backoffice.domain.model.Propiedad;
import com.resimanager.backoffice.domain.model.Propietario;
import com.resimanager.backoffice.domain.model.PropietarioId;
import com.resimanager.backoffice.persistance.repository.PersonaRepository;
import com.resimanager.backoffice.persistance.repository.PropiedadRepository;
import com.resimanager.backoffice.persistance.repository.PropietarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropietarioService {

    private final PropietarioRepository propietarioRepository;
    private final PersonaRepository personaRepository;
    private final PropiedadRepository propiedadRepository;

    public PropietarioListResponse getPropietarios(String estatus, Integer conjuntoId, String search, Integer page, Integer limit) {
        String estatusParam = (estatus != null && !estatus.isBlank()) ? estatus.trim() : null;

        PageRequest pageable = PageRequest.of(page - 1, limit);
        Page<Propietario> result = propietarioRepository.findAllWithFilters(estatusParam, conjuntoId, pageable);

        return PropietarioListResponse.builder()
                .data(result.getContent().stream().map(this::toDTO).toList())
                .total(result.getTotalElements())
                .page(page)
                .limit(limit)
                .build();
    }

    public PropietarioDTO getPropietarioById(Integer conjId, Integer perId) {
        PropietarioId id = new PropietarioId();
        id.setPptConjid(conjId);
        id.setPptPerid(perId);

        Propietario propietario = propietarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Propietario no encontrado"));
        return toDTO(propietario);
    }

    @Transactional
    public PropietarioDTO createPropietario(Integer conjId, Integer perId, Integer propiedadId,
                                            String fechaDesde, String executorUsername, String estacion) {
        if (propietarioRepository.existsById(new PropietarioId() {{
            setPptConjid(conjId);
            setPptPerid(perId);
        }})) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El propietario ya existe en este conjunto");
        }

        Persona ejecutor = personaRepository.findByPerUsuario(executorUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejecutor no encontrado"));

        Propietario propietario = new Propietario();
        PropietarioId id = new PropietarioId();
        id.setPptConjid(conjId);
        id.setPptPerid(perId);
        propietario.setId(id);
        propietario.setPptID(propiedadId);
        propietario.setPptFchDesde(java.time.LocalDate.parse(fechaDesde));
        propietario.setPptSts("A");
        propietario.setPptFchHorCrea(OffsetDateTime.now());
        propietario.setPptEstCrea(estacion);
        propietario.setPptFchHorMod(OffsetDateTime.now());
        propietario.setPptEstMod(estacion);

        return toDTO(propietarioRepository.save(propietario));
    }

    @Transactional
    public PropietarioDTO updatePropietario(Integer conjId, Integer perId, Integer propiedadId,
                                            String fechaDesde, String fechaHasta, String estatus,
                                            String executorUsername, String estacion) {
        PropietarioId id = new PropietarioId();
        id.setPptConjid(conjId);
        id.setPptPerid(perId);

        Propietario propietario = propietarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Propietario no encontrado"));

        Persona ejecutor = personaRepository.findByPerUsuario(executorUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejecutor no encontrado"));

        if (propiedadId != null) propietario.setPptID(propiedadId);
        if (fechaDesde != null) propietario.setPptFchDesde(java.time.LocalDate.parse(fechaDesde));
        if (fechaHasta != null) propietario.setPptFchHasta(java.time.LocalDate.parse(fechaHasta));
        if (estatus != null && (estatus.equals("A") || estatus.equals("I"))) propietario.setPptSts(estatus);

        propietario.setPptFchHorMod(OffsetDateTime.now());
        propietario.setPptEstMod(estacion);

        return toDTO(propietarioRepository.save(propietario));
    }

    @Transactional
    public Map<String, String> deletePropietario(Integer conjId, Integer perId, String executorUsername, String estacion) {
        PropietarioId id = new PropietarioId();
        id.setPptConjid(conjId);
        id.setPptPerid(perId);

        Propietario propietario = propietarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Propietario no encontrado"));

        propietario.setPptSts("I");
        propietario.setPptFchHorMod(OffsetDateTime.now());
        propietario.setPptEstMod(estacion);

        propietarioRepository.save(propietario);
        return Map.of("message", "Propietario inactivado correctamente");
    }

    private PropietarioDTO toDTO(Propietario p) {
        String nombrePersona = null;
        String apellidoPersona = null;
        String documentoPersona = null;
        String nombrePropiedad = null;

        Optional<Persona> persona = personaRepository.findById(p.getId().getPptPerid());
        if (persona.isPresent()) {
            nombrePersona = persona.get().getPerNombre();
            apellidoPersona = persona.get().getPerApellido();
            documentoPersona = persona.get().getPerDocIdent();
        }

        Optional<Propiedad> prop = propiedadRepository.findByPpidAndPpConjId(p.getPptID(), p.getId().getPptConjid());
        if (prop.isPresent()) {
            nombrePropiedad = prop.get().getPpNumero();
        }

        return PropietarioDTO.builder()
                .conjId(p.getId().getPptConjid())
                .perId(p.getId().getPptPerid())
                .personaNombre(nombrePersona)
                .personaApellido(apellidoPersona)
                .personaDocumento(documentoPersona)
                .propiedadId(p.getPptID())
                .propiedadNombre(nombrePropiedad)
                .fechaDesde(p.getPptFchDesde() != null ? p.getPptFchDesde().toString() : null)
                .fechaHasta(p.getPptFchHasta() != null ? p.getPptFchHasta().toString() : null)
                .estatus(p.getPptSts())
                .build();
    }
}
