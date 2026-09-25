package com.shahkabir.coffer.model;

public enum TransactionStatus {
    POSTED("Posted"),
    PENDING("Pending"),
    FAILED("Failed");

    private final String value;

    TransactionStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
