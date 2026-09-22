package com.shahkabir.coffer.generator;

import java.security.SecureRandom;

public class AccountNumberGenerator {
    private final SecureRandom random = new SecureRandom();

    public String generate() {
        long number = random.nextLong(1_000_000_000_000L);

        return String.format("%012d", number);
    }
}
