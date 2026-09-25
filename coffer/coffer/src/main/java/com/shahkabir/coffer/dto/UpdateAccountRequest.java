package com.shahkabir.coffer.dto;

import com.shahkabir.coffer.model.AccountStatus;
import com.shahkabir.coffer.model.CurrencyType;

public record UpdateAccountRequest (
        CurrencyType currency,
        AccountStatus status
) {
    public UpdateAccountRequest {
        if (currency == null && status == null) {
            throw new IllegalArgumentException("Updated information cannot be empty.");
        }
    }
}
