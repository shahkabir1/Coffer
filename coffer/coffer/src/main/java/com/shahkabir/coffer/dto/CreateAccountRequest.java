package com.shahkabir.coffer.dto;

import com.shahkabir.coffer.model.AccountType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateAccountRequest (
        @NotNull
        UUID customerId,
        @NotNull
        AccountType type
) {}
