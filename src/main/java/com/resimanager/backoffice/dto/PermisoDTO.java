package com.resimanager.backoffice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PermisoDTO {
    
    @JsonProperty("modulo_id")
    private Integer moduloId;
    
    @JsonProperty("modulo")
    private String modulo;
    
    @JsonProperty("acciones")
    private List<String> acciones;
}
