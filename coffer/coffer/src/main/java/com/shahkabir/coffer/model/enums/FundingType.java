package com.shahkabir.coffer.model.enums;

public enum FundingType {
    DEPOSIT("Deposit"),
    WITHDRAWAL("Withdrawal");

    private final String value;

    FundingType(String value) {
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
