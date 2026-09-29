package com.shahkabir.coffer.model.enums;

public enum Role {
    CUSTOMER("Customer"),
    ADMIN("Admin"),
    INTERNAL("Internal");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
