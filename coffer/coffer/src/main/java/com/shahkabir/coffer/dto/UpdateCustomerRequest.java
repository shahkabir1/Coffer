package com.shahkabir.coffer.dto;

public record UpdateCustomerRequest (
        String firstName,
        String lastName,
        String email
)
{
    public UpdateCustomerRequest {
        if (firstName == null && lastName == null && email == null) {
            throw new IllegalArgumentException("Updated information cannot be empty.");
        }
    }
}
