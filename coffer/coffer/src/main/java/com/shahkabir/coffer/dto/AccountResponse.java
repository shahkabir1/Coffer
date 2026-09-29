package com.shahkabir.coffer.dto;

import com.shahkabir.coffer.model.enums.AccountStatus;
import com.shahkabir.coffer.model.enums.AccountType;
import com.shahkabir.coffer.model.enums.CurrencyType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountResponse (
        UUID id,
        String maskedAccountNumber,
        AccountType type,
        CurrencyType currency,
        BigDecimal balance,
        AccountStatus status,
        UUID customerId,
        Instant createdAt
){}
