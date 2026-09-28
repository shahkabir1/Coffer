package com.shahkabir.coffer.dto;

import com.shahkabir.coffer.model.CurrencyType;
import com.shahkabir.coffer.model.FundingType;
import com.shahkabir.coffer.model.TransactionStatus;

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
