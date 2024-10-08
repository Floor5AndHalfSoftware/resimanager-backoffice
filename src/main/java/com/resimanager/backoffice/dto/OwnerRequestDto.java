package com.resimanager.backoffice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerRequestDto {

    private String name;
    private String phone;
    private String email;
    private String address;
}

