package com.shahkabir.coffer.repository;

import com.shahkabir.coffer.model.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LedgerEntryRepository extends JpaRepository<LedgerEntry,
        UUID> {
}
