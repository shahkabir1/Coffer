package com.shahkabir.coffer.dto;

import com.shahkabir.coffer.model.Customer;

public record RegisterRequest (
        String email,
        String rawPassword,
        Customer customer
) {}
