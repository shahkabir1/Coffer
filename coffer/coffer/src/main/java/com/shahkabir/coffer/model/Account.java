package com.shahkabir.coffer.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 12)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private CurrencyType currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private Instant createdAt;

    protected Account() {
    }

    public Account(String accountNumber, AccountType type, Customer customer) {
        this.accountNumber = accountNumber;
        this.type = type;
        this.currency = CurrencyType.CAD;
        this.status = AccountStatus.ACTIVE;
        this.customer = customer;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public AccountType getAccountType() {
        return type;
    }

    public CurrencyType getCurrency() {
        return currency;
    }

    public AccountStatus getAccountStatus() {
        return status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Instant getCreationTime() {
        return createdAt;
    }

    public void changeCurrencyType(CurrencyType newCurrency) {
        this.currency = newCurrency;
    }

    public void changeAccountStatus(AccountStatus newStatus) {
        this.status = newStatus;
    }
}
