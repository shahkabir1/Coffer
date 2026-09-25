package com.shahkabir.coffer.model;


import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "ledger_transactions")
public class LedgerTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "from_account_id", nullable = false)
    private Account fromAccount;

    @ManyToOne(optional = false)
    @JoinColumn(name = "to_account_id", nullable = false)
    private Account toAccount;

    @Column(name = "source_amount", nullable = false)
    private BigDecimal sourceAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_currency", nullable = false)
    private CurrencyType sourceCurrency;

    @Column(name = "destination_amount", nullable = false)
    private BigDecimal destinationAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "destination_currency", nullable = false)
    private CurrencyType destinationCurrency;

    @Column(name = "exchange_rate", nullable = false)
    private BigDecimal exchangeRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;


    public LedgerTransaction(Account fromAccount,
                             Account toAccount,
                             BigDecimal sourceAmount,
                             CurrencyType sourceCurrency,
                             BigDecimal destinationAmount,
                             CurrencyType destinationCurrency,
                             BigDecimal exchangeRate,
                             TransactionStatus status) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.sourceAmount = sourceAmount;
        this.sourceCurrency = sourceCurrency;
        this.destinationAmount = destinationAmount;
        this.destinationCurrency = destinationCurrency;
        this.exchangeRate = exchangeRate;
        this.status = status;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public Account getFromAccount() {
        return fromAccount;
    }

    public Account getToAccount() {
        return toAccount;
    }

    public BigDecimal getSourceAmount() {
        return sourceAmount;
    }

    public CurrencyType getSourceCurrency() {
        return sourceCurrency;
    }

    public BigDecimal getDestinationAmount() {
        return destinationAmount;
    }

    public CurrencyType getDestinationCurrency() {
        return destinationCurrency;
    }

    public BigDecimal getRate() {
        return exchangeRate;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public Instant getCreationTime() {
        return createdAt;
    }

    public void changeTransactionStatus(TransactionStatus newStatus) {
        this.status = newStatus;
    }
}
