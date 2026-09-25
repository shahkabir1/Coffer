package com.shahkabir.coffer.service;

import com.shahkabir.coffer.exception.EntryNotFoundException;
import com.shahkabir.coffer.exception.TransactionNotFoundException;
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

    public LedgerService(
            LedgerTransactionRepository transactionRepository,
            LedgerEntryRepository entryRepository) {
        this.transactionRepository = transactionRepository;
        this.entryRepository = entryRepository;
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
                TransactionStatus.POSTED);

        transactionRepository.save(transaction);

        LedgerEntry debitEntry = new LedgerEntry(transaction, fromAccount,
                EntryType.DEBIT, sourceAmount, fromAccount.getCurrency());

        LedgerEntry creditEntry = new LedgerEntry(transaction,
                toAccount, EntryType.CREDIT, destinationAmount,
                toAccount.getCurrency());

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
