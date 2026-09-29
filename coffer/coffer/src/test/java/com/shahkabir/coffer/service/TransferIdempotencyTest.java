package com.shahkabir.coffer.service;

import com.shahkabir.coffer.exception.IdempotencyConflictException;
import com.shahkabir.coffer.exception.InsufficientFundsException;
import com.shahkabir.coffer.model.LedgerTransaction;

import java.math.BigDecimal;
import java.util.UUID;

import com.shahkabir.coffer.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class TransferIdempotencyTest {

    @Autowired
    private TransferService transferService;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void duplicateRequestDoesNotTransferMoneyTwice() {

        UUID senderId = UUID.fromString("6842880c-9ded-4e6c-ac07-797747b2d6b6");
        UUID recipientId = UUID.fromString("a3fe54e2-1cff-4fd8-8912-d00e1c4a7267");

        String idempotencyKey = UUID.randomUUID().toString();

        BigDecimal senderBalanceBefore =
                accountRepository.findById(senderId)
                        .orElseThrow()
                        .getBalance();

        LedgerTransaction first = transferService.transfer(
                senderId,
                recipientId,
                new BigDecimal("20.00"),
                idempotencyKey
        );

        LedgerTransaction second = transferService.transfer(
                senderId,
                recipientId,
                new BigDecimal("20.00"),
                idempotencyKey
        );

        BigDecimal senderBalanceAfter =
                accountRepository.findById(senderId)
                        .orElseThrow()
                        .getBalance();

        assertEquals(first.getId(), second.getId());

        assertEquals(
                0,
                senderBalanceAfter.compareTo(
                        senderBalanceBefore.subtract(
                                new BigDecimal("20.00")
                        )
                )
        );
    }

    @Test
    void reusedKeyWithDifferentAmountThrowsConflict() {

        UUID senderId = UUID.fromString("6842880c-9ded-4e6c-ac07-797747b2d6b6");
        UUID recipientId = UUID.fromString("a3fe54e2-1cff-4fd8-8912-d00e1c4a7267");

        String idempotencyKey = UUID.randomUUID().toString();

        transferService.transfer(
                senderId,
                recipientId,
                new BigDecimal("20.00"),
                idempotencyKey
        );

        assertThrows(
                IdempotencyConflictException.class,
                () -> transferService.transfer(
                        senderId,
                        recipientId,
                        new BigDecimal("50.00"),
                        idempotencyKey
                )
        );
    }
}