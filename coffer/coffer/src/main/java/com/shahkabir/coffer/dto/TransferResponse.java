package com.shahkabir.coffer.dto;

import com.shahkabir.coffer.model.CurrencyType;
import com.shahkabir.coffer.model.TransactionStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferResponse (
        UUID transactionId,
        TransactionStatus status,
        UUID fromAccountId,
        UUID toAccountId,
        BigDecimal sourceAmount,
        CurrencyType sourceCurrency,
        BigDecimal destinationAmount,
        CurrencyType destinationCurrency,
        BigDecimal exchangeRate
) {}
