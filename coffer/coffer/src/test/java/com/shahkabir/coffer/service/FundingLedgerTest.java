package com.shahkabir.coffer.service;

import com.shahkabir.coffer.model.Account;
import com.shahkabir.coffer.model.LedgerEntry;
import com.shahkabir.coffer.model.LedgerTransaction;
import com.shahkabir.coffer.model.enums.EntryType;
import com.shahkabir.coffer.model.enums.TransactionStatus;
import com.shahkabir.coffer.repository.AccountRepository;
import com.shahkabir.coffer.repository.LedgerEntryRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FundingLedgerTest {

    @Autowired
    private FundingService fundingService;

    @Autowired
    private LedgerEntryRepository entryRepository;

    @Autowired
    private AccountRepository accountRepository;


    @Test
    void depositCreatesCorrectLedgerEntries() {

        UUID customerAccountId =
                UUID.fromString("6842880c-9ded-4e6c-ac07-797747b2d6b6");

        BigDecimal amount = new BigDecimal("50.00");

        LedgerTransaction transaction =
                fundingService.deposit(customerAccountId, amount, UUID.randomUUID().toString());

        List<LedgerEntry> entries =
                entryRepository.findByTransaction_Id(
                        transaction.getId()
                );

        assertEquals(
                TransactionStatus.POSTED,
                transaction.getStatus()
        );

        assertEquals(2, entries.size());

        LedgerEntry customerEntry = entries.stream()
                .filter(entry ->
                        entry.getAccount()
                                .getId()
                                .equals(customerAccountId)
                )
                .findFirst()
                .orElseThrow();

        LedgerEntry clearingEntry = entries.stream()
                .filter(entry ->
                        !entry.getAccount()
                                .getId()
                                .equals(customerAccountId)
                )
                .findFirst()
                .orElseThrow();

        assertEquals(
                EntryType.CREDIT,
                customerEntry.getEntryType()
        );

        assertEquals(
                0,
                customerEntry.getAmount()
                        .compareTo(amount)
        );

        assertEquals(
                EntryType.DEBIT,
                clearingEntry.getEntryType()
        );

        assertEquals(
                0,
                clearingEntry.getAmount()
                        .compareTo(amount)
        );
    }


    @Test
    void withdrawalCreatesCorrectLedgerEntries() {

        UUID customerAccountId =
                UUID.fromString("6842880c-9ded-4e6c-ac07-797747b2d6b6");

        BigDecimal amount = new BigDecimal("20.00");

        LedgerTransaction transaction =
                fundingService.withdraw(customerAccountId, amount, UUID.randomUUID().toString());

        List<LedgerEntry> entries =
                entryRepository.findByTransaction_Id(
                        transaction.getId()
                );

        assertEquals(
                TransactionStatus.POSTED,
                transaction.getStatus()
        );

        assertEquals(2, entries.size());

        LedgerEntry customerEntry = entries.stream()
                .filter(entry ->
                        entry.getAccount()
                                .getId()
                                .equals(customerAccountId)
                )
                .findFirst()
                .orElseThrow();

        LedgerEntry clearingEntry = entries.stream()
                .filter(entry ->
                        !entry.getAccount()
                                .getId()
                                .equals(customerAccountId)
                )
                .findFirst()
                .orElseThrow();

        assertEquals(
                EntryType.DEBIT,
                customerEntry.getEntryType()
        );

        assertEquals(
                0,
                customerEntry.getAmount()
                        .compareTo(amount)
        );

        assertEquals(
                EntryType.CREDIT,
                clearingEntry.getEntryType()
        );

        assertEquals(
                0,
                clearingEntry.getAmount()
                        .compareTo(amount)
        );
    }
}
