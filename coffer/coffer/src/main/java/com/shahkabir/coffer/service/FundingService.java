package com.shahkabir.coffer.service;

import com.shahkabir.coffer.exception.AccountInactiveException;
import com.shahkabir.coffer.exception.AccountNotFoundException;
import com.shahkabir.coffer.exception.InvalidAmountException;
import com.shahkabir.coffer.model.Account;
import com.shahkabir.coffer.model.AccountStatus;
import com.shahkabir.coffer.model.LedgerTransaction;
import com.shahkabir.coffer.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class FundingService {
    private final AccountRepository accountRepository;
    private final LedgerService ledgerService;

    public FundingService(
            AccountRepository accountRepository,
            LedgerService ledgerService
    ) {
        this.accountRepository = accountRepository;
        this.ledgerService = ledgerService;
    }

    public LedgerTransaction deposit(UUID accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "Account not found: " + accountId
                        )
                );

        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new AccountInactiveException("Deposits can only be made for active accounts");
        }

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException("Cannot deposit amount less than 0");
        }

        return ledgerService.postDeposit(account, amount);


    }
}
