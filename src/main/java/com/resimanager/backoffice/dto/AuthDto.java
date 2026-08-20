package com.resimanager.backoffice.dto;

import lombok.Builder;

import java.util.Set;

@Builder(toBuilder = true)
public record AuthDto(
    Integer userId,
    String username,
    String password,
    Set<String> authorities
) {}
