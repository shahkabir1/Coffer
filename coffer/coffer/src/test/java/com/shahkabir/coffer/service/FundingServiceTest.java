package com.shahkabir.coffer.service;

import com.shahkabir.coffer.model.Account;
import com.shahkabir.coffer.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class FundingServiceTest {

    @Autowired
    private FundingService fundingService;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void depositIncreasesBalance() {

        UUID accountId =
                UUID.fromString("6842880c-9ded-4e6c-ac07-797747b2d6b6");

        Account before = accountRepository.findById(accountId)
                .orElseThrow();

        BigDecimal startingBalance = before.getBalance();

        fundingService.deposit(
                accountId,
                new BigDecimal("50.00"),
                UUID.randomUUID().toString()
        );

        Account after = accountRepository.findById(accountId)
                .orElseThrow();

        assertEquals(
                0,
                after.getBalance().compareTo(
                        startingBalance.add(new BigDecimal("50.00"))
                )
        );
    }

    @Test
    void withdrawalDecreasesBalance() {

        UUID accountId =
                UUID.fromString("6842880c-9ded-4e6c-ac07-797747b2d6b6");

        BigDecimal startingBalance =
                accountRepository.findById(accountId)
                        .orElseThrow()
                        .getBalance();

        fundingService.withdraw(
                accountId,
                new BigDecimal("27.00"),
                UUID.randomUUID().toString()
        );

        BigDecimal endingBalance =
                accountRepository.findById(accountId)
                        .orElseThrow()
                        .getBalance();

        assertEquals(
                0,
                endingBalance.compareTo(
                        startingBalance.subtract(new BigDecimal("27.00"))
                )
        );
    }
}
