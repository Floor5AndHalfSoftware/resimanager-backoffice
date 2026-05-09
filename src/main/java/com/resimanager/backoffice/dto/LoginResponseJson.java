package com.resimanager.backoffice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseJson {
    private String type;
    private String token;
    private UserInfoDTO usuario;
    private List<ContextoDTO> contextosDisponibles;
}

