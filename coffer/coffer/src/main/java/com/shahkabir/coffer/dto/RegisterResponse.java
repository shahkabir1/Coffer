package com.shahkabir.coffer.dto;

import com.shahkabir.coffer.model.enums.Role;

import java.util.UUID;

public record RegisterResponse(
        UUID userId,
        String email,
        Role role,
        UUID customerId
) {}
