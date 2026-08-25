package com.resimanager.backoffice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateUsuarioRequest(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 80, message = "El nombre no puede exceder 80 caracteres")
    String nombre,
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 80, message = "El apellido no puede exceder 80 caracteres")
    String apellido,
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    String telefono,
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    @Size(max = 120, message = "El email no puede exceder 120 caracteres")
    String email,
    @Pattern(regexp = "^[AI]$", message = "El estatus debe ser 'A' (Activo) o 'I' (Inactivo)")
    String estatus
) {}
