package com.shahkabir.coffer.model;

public enum AccountType {
    CHEQUING("Chequing"),
    SAVINGS("Savings"),
    INTERNAL_CLEARING("Internal Clearing");

    private final String value;

    AccountType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
