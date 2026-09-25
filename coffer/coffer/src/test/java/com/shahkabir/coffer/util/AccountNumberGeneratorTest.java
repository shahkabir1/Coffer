package com.shahkabir.coffer.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountNumberGeneratorTest {

    @Test
    void shouldGenerateTwelveDigitAccountNum() {
        AccountNumberGenerator generator =
                new AccountNumberGenerator();

        String accountNumber = generator.generate();

        assertEquals(12, accountNumber.length());
        System.out.println(accountNumber);
    }
}
