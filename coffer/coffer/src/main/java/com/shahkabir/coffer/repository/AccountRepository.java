package com.shahkabir.coffer.repository;

import com.shahkabir.coffer.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
}
