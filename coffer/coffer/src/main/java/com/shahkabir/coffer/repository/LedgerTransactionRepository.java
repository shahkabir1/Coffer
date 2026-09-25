package com.shahkabir.coffer.repository;

import com.shahkabir.coffer.model.LedgerTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LedgerTransactionRepository
        extends JpaRepository<LedgerTransaction, UUID> {
}
