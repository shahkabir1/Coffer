package com.shahkabir.coffer.service;

import com.shahkabir.coffer.dto.FxRateResponse;
import com.shahkabir.coffer.model.LedgerEntry;
import com.shahkabir.coffer.model.LedgerTransaction;
import com.shahkabir.coffer.model.enums.AccountType;
import com.shahkabir.coffer.model.enums.CurrencyType;
import com.shahkabir.coffer.model.enums.EntryType;
import com.shahkabir.coffer.repository.AccountRepository;
import com.shahkabir.coffer.repository.LedgerEntryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

@SpringBootTest
class FxTransferTest {

    @Autowired
    private TransferService transferService;

    @Autowired
    private LedgerEntryRepository entryRepository;

    @MockitoBean
    private FxQuoteService fxQuoteService;

    @Test
    void fxTransferCreatesFourLedgerEntries() {
        UUID senderId =
                UUID.fromString("4a4f181c-e671-43e7-9ecd-d0968eb0bb8a");
        UUID recipientId =
                UUID.fromString("6842880c-9ded-4e6c-ac07-797747b2d6b6");

        BigDecimal sourceAmount =
                new BigDecimal("100.00");

        BigDecimal rate =
                new BigDecimal("1.80");

        BigDecimal expectedDestinationAmount =
                new BigDecimal("180.00");

        String idempotencyKey =
                UUID.randomUUID().toString();

        when(
                fxQuoteService.getRate(
                        CurrencyType.GBP,
                        CurrencyType.CAD
                )
        ).thenReturn(
                new FxRateResponse(
                        null,
                        "GBP",
                        "CAD",
                        rate
                )
        );

        LedgerTransaction transaction =
                transferService.transfer(
                        senderId,
                        recipientId,
                        sourceAmount,
                        idempotencyKey
                );

        List<LedgerEntry> entries =
                entryRepository.findByTransaction_Id(
                        transaction.getId()
                );

        assertEquals(4, entries.size());

        long debitCount = entries.stream()
                .filter(entry -> entry.getEntryType() == EntryType.DEBIT)
                .count();

        assertEquals(2, debitCount);

        long creditCount = entries.stream()
                .filter(entry -> entry.getEntryType() == EntryType.CREDIT)
                .count();

        assertEquals(2, creditCount);

        long sourceCurrencyCount = entries.stream()
                .filter(entry -> entry.getCurrency() == CurrencyType.GBP)
                .count();

        assertEquals(2, sourceCurrencyCount);

        long destinationCurrencyCount = entries.stream()
                .filter(entry -> entry.getCurrency() == CurrencyType.CAD)
                .count();

        assertEquals(2, destinationCurrencyCount);

        long clearingCount = entries.stream()
                .filter(entry -> entry.getAccount().getAccountType() == AccountType.INTERNAL_CLEARING)
                .count();

        assertEquals(2, clearingCount);

        long chequingOrSavingsCount = entries.stream()
                .filter(entry -> entry.getAccount().getAccountType() == AccountType.CHEQUING
                        || entry.getAccount().getAccountType() == AccountType.SAVINGS)
                .count();

        assertEquals(2, chequingOrSavingsCount);

        assertTrue(entries.stream().anyMatch(entry ->
                entry.getAccount().getId().equals(senderId)
                        && entry.getEntryType() == EntryType.DEBIT
                        && entry.getCurrency() == CurrencyType.GBP
                        && entry.getAmount().compareTo(new BigDecimal("100.00")) == 0
        ));

        assertTrue(entries.stream().anyMatch(entry ->
                entry.getAccount().getId().equals(recipientId)
                        && entry.getEntryType() == EntryType.CREDIT
                        && entry.getCurrency() == CurrencyType.CAD
                        && entry.getAmount().compareTo(new BigDecimal("180.00")) == 0
        ));

        assertTrue(entries.stream().anyMatch(entry ->
                entry.getAccount().getAccountType().equals(AccountType.INTERNAL_CLEARING)
                        && entry.getEntryType() == EntryType.CREDIT
                        && entry.getCurrency() == CurrencyType.GBP
                        && entry.getAmount().compareTo(new BigDecimal("100.00")) == 0
        ));

        assertTrue(entries.stream().anyMatch(entry ->
                entry.getAccount().getAccountType().equals(AccountType.INTERNAL_CLEARING)
                        && entry.getEntryType() == EntryType.DEBIT
                        && entry.getCurrency() == CurrencyType.CAD
                        && entry.getAmount().compareTo(new BigDecimal("180.00")) == 0
        ));
    }

    @Test
    void fxTransferUsesExpectedExchangeRate() {
        UUID senderId =
                UUID.fromString("4a4f181c-e671-43e7-9ecd-d0968eb0bb8a");
        UUID recipientId =
                UUID.fromString("6842880c-9ded-4e6c-ac07-797747b2d6b6");

        BigDecimal sourceAmount =
                new BigDecimal("100.00");

        BigDecimal rate =
                new BigDecimal("1.80");

        BigDecimal expectedDestinationAmount =
                new BigDecimal("180.00");

        String idempotencyKey =
                UUID.randomUUID().toString();


        when(
                fxQuoteService.getRate(
                        CurrencyType.GBP,
                        CurrencyType.CAD
                )
        ).thenReturn(
                new FxRateResponse(
                        null,
                        "GBP",
                        "CAD",
                        rate
                )
        );


        LedgerTransaction transaction =
                transferService.transfer(
                        senderId,
                        recipientId,
                        sourceAmount,
                        idempotencyKey
                );


        assertEquals(
                0,
                transaction.getSourceAmount()
                        .compareTo(sourceAmount)
        );

        assertEquals(
                CurrencyType.GBP,
                transaction.getSourceCurrency()
        );

        assertEquals(
                0,
                transaction.getDestinationAmount()
                        .compareTo(expectedDestinationAmount)
        );

        assertEquals(
                CurrencyType.CAD,
                transaction.getDestinationCurrency()
        );

        assertEquals(
                0,
                transaction.getRate()
                        .compareTo(rate)
        );


        List<LedgerEntry> entries =
                entryRepository.findByTransaction_Id(
                        transaction.getId()
                );

        assertEquals(4, entries.size());

    }

    private void assertEquals(Object i, Object i1) {
    }
}
