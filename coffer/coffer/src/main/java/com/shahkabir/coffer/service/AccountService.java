package com.shahkabir.coffer.service;

import com.shahkabir.coffer.generator.AccountNumberGenerator;
import com.shahkabir.coffer.model.Account;
import com.shahkabir.coffer.model.Customer;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
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
    public Account createAccount(UUID customerId) {

        Customer customer = customerRepository
                .findById(customerId)
                .orElseThrow(() ->
                        new NoSuchElementException("Customer not found")
                );

        String accountNumber = accountNumberGenerator.generate();

        Account account = new Account(accountNumber, customer);

        return accountRepository.save(acccount);
    }

    @Transactional(readOnly = true)
    public Account getAccount(UUID accountId) {
        return accountRepository.findById(accouhtId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Account not found"
                        )
                );
    }
}
