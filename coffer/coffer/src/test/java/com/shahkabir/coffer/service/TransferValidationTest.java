package com.shahkabir.coffer.service;

import com.shahkabir.coffer.exception.AccountInactiveException;
import com.shahkabir.coffer.exception.InsufficientFundsException;
import com.shahkabir.coffer.exception.InvalidAmountException;
import com.shahkabir.coffer.exception.InvalidTransferException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class TransferValidationTest {

    @Autowired
    private TransferService transferService;

    private final UUID senderId =
            UUID.fromString("a3fe54e2-1cff-4fd8-8912-d00e1c4a7267");

    private final UUID recipientId =
            UUID.fromString("6501584c-06ab-40fe-855d-7497a7db43a7");


    @Test
    void transferFailsWhenBalanceIsInsufficient() {

        assertThrows(
                InsufficientFundsException.class,
                () -> transferService.transfer(
                        senderId,
                        recipientId,
                        new BigDecimal("999999.00"),
                        UUID.randomUUID().toString()
                )
        );
    }


    @Test
    void transferFailsWhenAmountIsInvalid() {

        assertThrows(
                InvalidAmountException.class,
                () -> transferService.transfer(
                        senderId,
                        recipientId,
                        BigDecimal.ZERO,
                        UUID.randomUUID().toString()
                )
        );
    }


    @Test
    void transferFailsWhenSenderEqualsRecipient() {

        assertThrows(
                InvalidTransferException.class,
                () -> transferService.transfer(
                        senderId,
                        senderId,
                        new BigDecimal("10.00"),
                        UUID.randomUUID().toString()
                )
        );
    }


    @Test
    void transferFailsWhenSenderInactive() {

        UUID inactiveSenderId =
                UUID.fromString("7bfe8bbf-11b1-4632-a37d-11a5235ec6da");

        assertThrows(
                AccountInactiveException.class,
                () -> transferService.transfer(
                        inactiveSenderId,
                        recipientId,
                        new BigDecimal("10.00"),
                        UUID.randomUUID().toString()
                )
        );
    }


    @Test
    void transferFailsWhenRecipientInactive() {

        UUID inactiveRecipientId =
                UUID.fromString("80937c93-2efe-456a-999c-9f2d26686063");

        assertThrows(
                AccountInactiveException.class,
                () -> transferService.transfer(
                        senderId,
                        inactiveRecipientId,
                        new BigDecimal("10.00"),
                        UUID.randomUUID().toString()
                )
        );
    }
}
