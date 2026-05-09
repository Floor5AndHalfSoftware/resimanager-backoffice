package com.resimanager.backoffice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConjuntoListResponse {
    private List<ConjuntoDTO> data;
    private Long total;
    private Integer page;
    private Integer limit;
}
