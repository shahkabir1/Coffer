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

    @Column(nullable = false)
    private AccountType type;

    @Column(nullable = false)
    private CurrencyType currency;

    @Column(nullable = false)
    private AccountStatus status;

    @Column(nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private Instant createdAt;

    public Account(String accountNumber, Customer customer) {
        this.accountNumber = accountNumber;
        this.type = AccountType.CHEQUING;
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

    public CurrencyType getCurrencyType() {
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
