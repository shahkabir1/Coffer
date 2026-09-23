package com.shahkabir.coffer.model;

public enum AccountStatus {
    ACTIVE("Active"),
    FROZEN("Frozen"),
    CLOSED("Closed");

    private final String value;

    AccountStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
