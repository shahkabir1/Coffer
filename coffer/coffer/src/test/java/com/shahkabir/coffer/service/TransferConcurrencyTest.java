package com.shahkabir.coffer.service;

import com.shahkabir.coffer.exception.InsufficientFundsException;
import com.shahkabir.coffer.model.Account;
import com.shahkabir.coffer.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class TransferConcurrencyTest {

    @Autowired
    private TransferService transferService;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void concurrentTransfersCannotOverspend() throws Exception {

        UUID senderId = UUID.fromString("6842880c-9ded-4e6c-ac07-797747b2d6b6");
        UUID recipientOneId = UUID.fromString("a3fe54e2-1cff-4fd8-8912-d00e1c4a7267");
        UUID recipientTwoId = UUID.fromString("6501584c-06ab-40fe-855d-7497a7db43a7");

        ExecutorService executor = Executors.newFixedThreadPool(2);

        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        Callable<Boolean> transferOne = () -> {
            ready.countDown();
            start.await();

            try {
                transferService.transfer(
                        senderId,
                        recipientOneId,
                        new BigDecimal("80.00"),
                        UUID.randomUUID().toString()
                );

                return true;
            } catch (InsufficientFundsException ex) {
                return false;
            }
        };

        Callable<Boolean> transferTwo = () -> {
            ready.countDown();
            start.await();

            try {
                transferService.transfer(
                        senderId,
                        recipientTwoId,
                        new BigDecimal("80.00"),
                        UUID.randomUUID().toString()
                );

                return true;
            } catch (InsufficientFundsException ex) {
                return false;
            }
        };

        Future<Boolean> resultOne = executor.submit(transferOne);
        Future<Boolean> resultTwo = executor.submit(transferTwo);

        // Wait until both threads are ready
        ready.await();

        // Release both at essentially the same time
        start.countDown();

        boolean firstSucceeded = resultOne.get();
        boolean secondSucceeded = resultTwo.get();

        executor.shutdown();

        Account sender = accountRepository.findById(senderId)
                .orElseThrow();

        assertEquals(
                1,
                (firstSucceeded ? 1 : 0) +
                        (secondSucceeded ? 1 : 0)
        );

        assertEquals(
                0,
                sender.getBalance().compareTo(new BigDecimal("20.00"))
        );
    }
}