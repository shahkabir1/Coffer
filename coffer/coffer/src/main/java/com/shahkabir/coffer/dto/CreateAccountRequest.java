package com.shahkabir.coffer.dto;

import com.shahkabir.coffer.model.AccountType;

import java.util.UUID;

public record CreateAccountRequest (
        UUID customerId,
        AccountType type
) {}
