package com.shahkabir.coffer.model;

public enum EntryType {
    DEBIT("Debit"),
    CREDIT("Credit");

    private final String value;

    EntryType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
