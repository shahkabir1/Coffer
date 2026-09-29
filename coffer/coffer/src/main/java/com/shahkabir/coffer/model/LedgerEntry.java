package com.shahkabir.coffer.model;


import com.shahkabir.coffer.model.enums.CurrencyType;
import com.shahkabir.coffer.model.enums.EntryType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ledger_entries")
public class LedgerEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    private LedgerTransaction transaction;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false)
    private EntryType entryType;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private CurrencyType currency;

    protected LedgerEntry(){
    }


    public LedgerEntry(LedgerTransaction transaction,
                       Account account, EntryType entryType, BigDecimal amount,
                       CurrencyType currency) {
        this.transaction = transaction;
        this.account = account;
        this.entryType = entryType;
        this.amount = amount;
        this.currency = currency;
    }

    public UUID getId() {
        return id;
    }

    public LedgerTransaction getTransaction() {
        return transaction;
    }

    public Account getAccount() {
        return account;
    }

    public EntryType getEntryType() {
        return entryType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public CurrencyType getCurrency() {
        return currency;
    }
}
