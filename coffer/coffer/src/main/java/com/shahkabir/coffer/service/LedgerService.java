package com.shahkabir.coffer.service;

import com.shahkabir.coffer.exception.*;
import com.shahkabir.coffer.model.*;
import com.shahkabir.coffer.model.enums.*;
import com.shahkabir.coffer.repository.AccountRepository;
import com.shahkabir.coffer.repository.LedgerEntryRepository;
import com.shahkabir.coffer.repository.LedgerTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

@Service
public class LedgerService {
    private final LedgerTransactionRepository transactionRepository;
    private final LedgerEntryRepository entryRepository;
    private final AccountRepository accountRepository;

    public LedgerService(
            LedgerTransactionRepository transactionRepository,
            LedgerEntryRepository entryRepository,
            AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.entryRepository = entryRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public LedgerTransaction postTransfer(UUID fromAccountId, UUID toAccountId,
                             BigDecimal sourceAmount,
                             CurrencyType sourceCurrency,
                             BigDecimal destinationAmount,
                             CurrencyType destinationCurrency,
                             BigDecimal rate, String idempotencyKey) {

            LockedAccounts accounts = lockAccounts(fromAccountId, toAccountId);

            Account fromAccount = accounts.fromAccount();
            Account toAccount = accounts.toAccount();

            LedgerTransaction transaction = new LedgerTransaction(fromAccount,
                    toAccount,
                    sourceAmount,
                    sourceCurrency,
                    destinationAmount, destinationCurrency,
                    rate,
                    TransactionStatus.PENDING,
                    idempotencyKey);

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

        if (sourceCurrency != destinationCurrency) {
            Account clearingAccount1 = accountRepository.findByTypeAndCurrency(
                            AccountType.INTERNAL_CLEARING,
                            sourceCurrency
                    )
                    .orElseThrow(() ->
                            new AccountNotFoundException(
                                    "No Internal Clearing accounts exist"
                            )
                    );

            Account clearingAccount2 = accountRepository.findByTypeAndCurrency(
                            AccountType.INTERNAL_CLEARING,
                            destinationCurrency
                    )
                    .orElseThrow(() ->
                            new AccountNotFoundException(
                                    "No Internal Clearing accounts exist"
                            )
                    );

            fromAccount.setBalance(
                    fromAccount.getBalance().subtract(sourceAmount)
            );

            clearingAccount1.setBalance(
                    clearingAccount1.getBalance().add(sourceAmount)
            );

            clearingAccount2.setBalance(
                    clearingAccount2.getBalance().subtract(destinationAmount)
            );

            toAccount.setBalance(
                    toAccount.getBalance().add(destinationAmount)
            );

            transactionRepository.save(transaction);

            LedgerEntry debitEntry1 = new LedgerEntry(transaction, fromAccount,
                    EntryType.DEBIT, sourceAmount, fromAccount.getCurrency());

            LedgerEntry creditEntry1 = new LedgerEntry(transaction,
                    clearingAccount1, EntryType.CREDIT, sourceAmount,
                    clearingAccount1.getCurrency());

            LedgerEntry debitEntry2 = new LedgerEntry(transaction, clearingAccount2,
                    EntryType.DEBIT, destinationAmount, clearingAccount2.getCurrency());

            LedgerEntry creditEntry2 = new LedgerEntry(transaction,
                    toAccount, EntryType.CREDIT, destinationAmount,
                    toAccount.getCurrency());

            entryRepository.saveAll(List.of(debitEntry1, creditEntry1, debitEntry2, creditEntry2));
            return transaction;
        }

            LedgerEntry debitEntry = new LedgerEntry(transaction, fromAccount,
                    EntryType.DEBIT, sourceAmount, fromAccount.getCurrency());

            LedgerEntry creditEntry = new LedgerEntry(transaction,
                    toAccount, EntryType.CREDIT, destinationAmount,
                    toAccount.getCurrency());

            entryRepository.saveAll(List.of(debitEntry, creditEntry));
            return transaction;
    }

    @Transactional
    public LedgerTransaction postDeposit(UUID customerAccountId, BigDecimal amount, String idempotencyKey) {

        Account customerAccount = accountRepository.findByIdForUpdate(customerAccountId)
                .orElseThrow(() ->
                        new NoSuchElementException("Customer account not found")
                );

        Account clearingAccount = accountRepository.findByTypeAndCurrency(
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
                TransactionStatus.PENDING,
                idempotencyKey);

        clearingAccount.setBalance(
                clearingAccount.getBalance().subtract(amount)
        );

        customerAccount.setBalance(
                customerAccount.getBalance().add(amount)
        );

        transaction.changeTransactionStatus(TransactionStatus.POSTED);

        transactionRepository.save(transaction);

        LedgerEntry debitEntry = new LedgerEntry(transaction, clearingAccount,
                EntryType.DEBIT, amount, clearingAccount.getCurrency());

        LedgerEntry creditEntry = new LedgerEntry(transaction,
                customerAccount, EntryType.CREDIT, amount,
                customerAccount.getCurrency());

        entryRepository.saveAll(List.of(debitEntry, creditEntry));

        return transaction;
    }

    @Transactional
    public LedgerTransaction postWithdrawal(UUID customerAccountId, BigDecimal amount, String idempotencyKey) {

        Account customerAccount = accountRepository.findByIdForUpdate(customerAccountId)
                .orElseThrow(() ->
                        new NoSuchElementException("Customer account not found")
                );

        Account clearingAccount = accountRepository.findByTypeAndCurrency(
                        AccountType.INTERNAL_CLEARING,
                        customerAccount.getCurrency()
                )
                .orElseThrow(() ->
                        new AccountNotFoundException(
                                "No Internal Clearing accounts exist"
                        )
                );

        LedgerTransaction transaction = new LedgerTransaction(customerAccount,
                clearingAccount,
                amount,
                customerAccount.getCurrency(),
                amount, customerAccount.getCurrency(),
                BigDecimal.ONE,
                TransactionStatus.PENDING,
                idempotencyKey);

        clearingAccount.setBalance(
                clearingAccount.getBalance().add(amount)
        );

        customerAccount.setBalance(
                customerAccount.getBalance().subtract(amount)
        );

        transaction.changeTransactionStatus(TransactionStatus.POSTED);

        transactionRepository.save(transaction);

        LedgerEntry debitEntry = new LedgerEntry(transaction, customerAccount,
                EntryType.DEBIT, amount, clearingAccount.getCurrency());

        LedgerEntry creditEntry = new LedgerEntry(transaction,
                clearingAccount, EntryType.CREDIT, amount,
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

    private LockedAccounts lockAccounts(
            UUID fromAccountId,
            UUID toAccountId
    ) {
        UUID firstId = fromAccountId.compareTo(toAccountId) < 0
                ? fromAccountId
                : toAccountId;

        UUID secondId = firstId.equals(fromAccountId)
                ? toAccountId
                : fromAccountId;

        Account first = accountRepository.findByIdForUpdate(firstId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found: " + firstId
                ));

        Account second = accountRepository.findByIdForUpdate(secondId)
                .orElseThrow(() -> new AccountNotFoundException(
                        "Account not found: " + secondId
                ));

        Account fromAccount = first.getId().equals(fromAccountId)
                ? first
                : second;

        Account toAccount = first.getId().equals(toAccountId)
                ? first
                : second;

        return new LockedAccounts(fromAccount, toAccount);
    }

    private record LockedAccounts(
            Account fromAccount,
            Account toAccount
    ){}
}
