package com.shahkabir.coffer.service;

import com.shahkabir.coffer.dto.FxRateResponse;
import com.shahkabir.coffer.model.Account;
import com.shahkabir.coffer.model.CurrencyType;
import com.shahkabir.coffer.model.LedgerTransaction;
import com.shahkabir.coffer.model.TransactionStatus;
import com.shahkabir.coffer.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;


@Service
public class TransferService {
    private final AccountRepository accountRepository;
    private final FxQuoteService fxQuoteService;
    private final LedgerService ledgerService;

    public TransferService(
            AccountRepository accountRepository,
            FxQuoteService fxQuoteService,
            LedgerService ledgerService) {
        this.accountRepository = accountRepository;
        this.fxQuoteService = fxQuoteService;
        this.ledgerService = ledgerService;
    }

    public LedgerTransaction transfer(
            UUID fromAccountId,
            UUID toAccountId,
            BigDecimal amount
    ) {

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

        return ledgerService.postTransfer(sender,
                recipient, amount, senderCurrency, recipientAmount,
                recipientCurrency, response.rate());
    }

}
