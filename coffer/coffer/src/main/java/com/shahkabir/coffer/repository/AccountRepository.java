package com.shahkabir.coffer.repository;

import com.shahkabir.coffer.model.Account;
import com.shahkabir.coffer.model.AccountType;
import com.shahkabir.coffer.model.CurrencyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByAccountTypeAndCurrency(
            AccountType accountType,
            CurrencyType currency
    );
}
