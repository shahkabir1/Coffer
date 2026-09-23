package com.shahkabir.coffer.model;

public enum CurrencyType {
    CAD("CAD"),
    USD("USD"),
    EUR("EUR"),
    GBP("GBP");

    private final String value;

    CurrencyType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
