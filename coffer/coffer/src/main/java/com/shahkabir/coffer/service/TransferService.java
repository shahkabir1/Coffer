package com.shahkabir.coffer.service;

import com.shahkabir.coffer.dto.FxRateResponse;
import com.shahkabir.coffer.exception.IdempotencyConflictException;
import com.shahkabir.coffer.exception.InvalidAmountException;
import com.shahkabir.coffer.model.Account;
import com.shahkabir.coffer.model.enums.CurrencyType;
import com.shahkabir.coffer.model.LedgerTransaction;
import com.shahkabir.coffer.repository.AccountRepository;
import com.shahkabir.coffer.repository.LedgerTransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;


@Service
public class TransferService {
    private final AccountRepository accountRepository;
    private final FxQuoteService fxQuoteService;
    private final LedgerService ledgerService;
    private final LedgerTransactionRepository transactionRepository;

    public TransferService(
            AccountRepository accountRepository,
            FxQuoteService fxQuoteService,
            LedgerService ledgerService,
            LedgerTransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.fxQuoteService = fxQuoteService;
        this.ledgerService = ledgerService;
        this.transactionRepository = transactionRepository;
    }

    public LedgerTransaction transfer(
            UUID fromAccountId,
            UUID toAccountId,
            BigDecimal amount,
            String idempotencyKey
            ) {

        Optional<LedgerTransaction> existing = transactionRepository
                .findByIdempotencyKey(idempotencyKey);

        if (existing.isPresent()) {
            LedgerTransaction transaction = existing.get();

            boolean sameRequest =
                    transaction.getFromAccount().getId().equals(fromAccountId)
                            && transaction.getToAccount().getId().equals(toAccountId)
                            && transaction.getSourceAmount().compareTo(amount) == 0;

            if (!sameRequest) {
                throw new IdempotencyConflictException(
                        "Idempotency key has already been used for a different request"
                );
            }

            return transaction;

        }



        if (amount.compareTo(new BigDecimal("0.01")) < 0) {
            throw new InvalidAmountException("Transfer amount must be at least 0.01");
        }

        Account sender = accountRepository
                .findById(fromAccountId)
                .orElseThrow(() ->
                        new NoSuchElementException("Account not found")
                );

        CurrencyType senderCurrency = sender.getCurrency();

        Account recipient = accountRepository
                .findById(toAccountId)
                .orElseThrow(() ->
                        new NoSuchElementException("Account not found")
                );

        CurrencyType recipientCurrency = recipient.getCurrency();

        FxRateResponse response = fxQuoteService.getRate(senderCurrency,
                recipientCurrency);

        BigDecimal recipientAmount = amount.multiply(response.rate());

        return ledgerService.postTransfer(fromAccountId,
                toAccountId, amount,
                senderCurrency, recipientAmount,
                recipientCurrency, response.rate(), idempotencyKey);
    }

}
