package com.shahkabir.coffer.dto;

import com.shahkabir.coffer.model.enums.CurrencyType;
import com.shahkabir.coffer.model.enums.FundingType;
import com.shahkabir.coffer.model.enums.TransactionStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record FundingResponse (
        UUID transactionId,
        TransactionStatus status,
        FundingType type,
        UUID accountId,
        BigDecimal amount,
        CurrencyType currency
)
{}
