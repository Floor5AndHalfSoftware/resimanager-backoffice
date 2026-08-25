package com.resimanager.backoffice.domain.port.in;

import com.resimanager.backoffice.domain.model.PerfilesUsuario;
import com.resimanager.backoffice.domain.model.Persona;
import com.resimanager.backoffice.domain.model.ResultadoPaginado;
import com.resimanager.backoffice.domain.model.enums.Estatus;

import java.util.Optional;

/**
 * Casos de uso de gestión de usuarios (personas).
 */
public interface UsuarioUseCase {

    ResultadoPaginado<Persona> obtenerUsuarios(Estatus estatus, String busqueda, int pagina, int limite);

    Optional<Persona> obtenerUsuario(Integer usuarioId);

    PerfilesUsuario obtenerPerfilesDeUsuario(Integer personaId);

    Persona actualizarUsuario(Integer id, String nombre, String apellido, String telefono,
                              String email, Estatus estatus, String ejecutor, String estacion);

    void inactivarUsuario(Integer id, String ejecutor, String estacion);
}