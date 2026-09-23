package com.shahkabir.coffer.service;

import com.shahkabir.coffer.generator.AccountNumberGenerator;
import com.shahkabir.coffer.model.Account;
import com.shahkabir.coffer.model.AccountType;
import com.shahkabir.coffer.model.Customer;
import com.shahkabir.coffer.repository.AccountRepository;
import com.shahkabir.coffer.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.List;
import java.util.UUID;

@Service
public class AccountService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final AccountNumberGenerator accountNumberGenerator;

    public AccountService(
            AccountRepository accountRepository,
            CustomerRepository customerRepository,
            AccountNumberGenerator accountNumberGenerator) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.accountNumberGenerator = accountNumberGenerator;
    }

    @Transactional
    public Account createAccount(UUID customerId, AccountType type) {

        Customer customer = customerRepository
                .findById(customerId)
                .orElseThrow(() ->
                        new NoSuchElementException("Customer not found")
                );

        String accountNumber = accountNumberGenerator.generate();

        Account account = new Account(accountNumber, type, customer);

        return accountRepository.save(account);
    }


    @Transactional(readOnly = true)
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Account getAccount(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Account not found"
                        )
                );
    }
}
