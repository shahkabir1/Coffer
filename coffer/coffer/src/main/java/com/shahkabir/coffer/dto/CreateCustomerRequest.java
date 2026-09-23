package com.shahkabir.coffer.dto;

public record CreateCustomerRequest (
    String firstName,
    String lastName,
    String email
) {}
