package com.shahkabir.coffer.service;

import com.shahkabir.coffer.exception.*;
import com.shahkabir.coffer.model.*;
import com.shahkabir.coffer.repository.AccountRepository;
import com.shahkabir.coffer.repository.LedgerEntryRepository;
import com.shahkabir.coffer.repository.LedgerTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class LedgerService {
    private final LedgerTransactionRepository transactionRepository;
    private final LedgerEntryRepository entryRepository;
    private final AccountRepository accountRepository;

    public LedgerService(
            LedgerTransactionRepository transactionRepository,
            LedgerEntryRepository entryRepository) {
        this.transactionRepository = transactionRepository;
        this.entryRepository = entryRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public LedgerTransaction postTransfer(Account fromAccount, Account toAccount,
                             BigDecimal sourceAmount,
                             CurrencyType sourceCurrency,
                             BigDecimal destinationAmount,
                             CurrencyType destinationCurrency,
                             BigDecimal rate) {

        LedgerTransaction transaction = new LedgerTransaction(fromAccount,
                toAccount,
                sourceAmount,
                sourceCurrency,
                destinationAmount, destinationCurrency,
                rate,
                TransactionStatus.PENDING);

        if (fromAccount.getAccountStatus() != AccountStatus.ACTIVE) {
            transaction.changeTransactionStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new AccountInactiveException("Sender account is not active");
        }

        if (toAccount.getAccountStatus() != AccountStatus.ACTIVE) {
            transaction.changeTransactionStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);
            throw new AccountInactiveException("Recipient account is not active");
        }


        if (fromAccount.getBalance().compareTo(sourceAmount) < 0) {
            transaction.changeTransactionStatus(TransactionStatus.FAILED);
            transactionRepository.save(transaction);

            throw new InsufficientFundsException("Insufficient funds");
        }

        transaction.changeTransactionStatus(TransactionStatus.POSTED);

        fromAccount.setBalance(
                fromAccount.getBalance().subtract(sourceAmount)
        );

        toAccount.setBalance(
                toAccount.getBalance().add(destinationAmount)
        );

        transactionRepository.save(transaction);

        LedgerEntry debitEntry = new LedgerEntry(transaction, fromAccount,
                EntryType.DEBIT, sourceAmount, fromAccount.getCurrency());

        LedgerEntry creditEntry = new LedgerEntry(transaction,
                toAccount, EntryType.CREDIT, destinationAmount,
                toAccount.getCurrency());

        entryRepository.saveAll(List.of(debitEntry, creditEntry));

        return transaction;
    }

    @Transactional
    public LedgerTransaction postDeposit(Account customerAccount, BigDecimal amount) {

        Account clearingAccount = accountRepository.findByAccountTypeAndCurrency(
                AccountType.INTERNAL_CLEARING,
                customerAccount.getCurrency()
        )
                .orElseThrow(() ->
                new AccountNotFoundException(
                        "No Internal Clearing accounts exist"
                )
        );

        LedgerTransaction transaction = new LedgerTransaction(clearingAccount,
                customerAccount,
                amount,
                customerAccount.getCurrency(),
                amount, customerAccount.getCurrency(),
                BigDecimal.ONE,
                TransactionStatus.POSTED);

        clearingAccount.setBalance(
                clearingAccount.getBalance().subtract(amount)
        );

        customerAccount.setBalance(
                customerAccount.getBalance().add(amount)
        );

        transactionRepository.save(transaction);

        LedgerEntry debitEntry = new LedgerEntry(transaction, clearingAccount,
                EntryType.DEBIT, amount, clearingAccount.getCurrency());

        LedgerEntry creditEntry = new LedgerEntry(transaction,
                customerAccount, EntryType.CREDIT, amount,
                customerAccount.getCurrency());

        entryRepository.saveAll(List.of(debitEntry, creditEntry));

        return transaction;
    }

    @Transactional(readOnly = true)
    public List<LedgerTransaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public LedgerTransaction getTransaction(UUID transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() ->
                        new TransactionNotFoundException(
                                "Transaction not found: " + transactionId
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<LedgerEntry> getAllEntries() {
        return entryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public LedgerEntry getEntry(UUID entryId) {
        return entryRepository.findById(entryId)
                .orElseThrow(() ->
                        new EntryNotFoundException(
                                "Entry not found: " + entryId
                        )
                );
    }
}
