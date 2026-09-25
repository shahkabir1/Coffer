
CREATE TABLE ledger_transactions (
    id UUID PRIMARY KEY,

    from_account_id UUID NOT NULL,
    to_account_id UUID NOT NULL,

    source_amount NUMERIC(19, 4) NOT NULL,
    source_currency VARCHAR(3) NOT NULL,

    destination_amount NUMERIC(19, 4) NOT NULL,
    destination_currency VARCHAR(3) NOT NULL,

    exchange_rate NUMERIC(20, 12) NOT NULL,
    rate_date DATE,

    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_transaction_from_account
        FOREIGN KEY (from_account_id)
        REFERENCES accounts(id),

    CONSTRAINT fk_transaction_to_account
        FOREIGN KEY (to_account_id)
        REFERENCES accounts(id),

    CONSTRAINT chk_transaction_source_amount
        CHECK (source_amount > 0),

    CONSTRAINT chk_transaction_destination_amount
        CHECK (destination_amount > 0),

    CONSTRAINT chk_transaction_exchange_rate
        CHECK (exchange_rate > 0),

    CONSTRAINT chk_transaction_different_accounts
        CHECK (from_account_id <> to_account_id)
);


CREATE TABLE ledger_entries (
    id UUID PRIMARY KEY,

    transaction_id UUID NOT NULL,
    account_id UUID NOT NULL,

    entry_type VARCHAR(20) NOT NULL,
    amount NUMERIC(19, 4) NOT NULL,
    currency VARCHAR(3) NOT NULL,

    CONSTRAINT fk_entry_transaction
        FOREIGN KEY (transaction_id)
        REFERENCES ledger_transactions(id),

    CONSTRAINT fk_entry_account
        FOREIGN KEY (account_id)
        REFERENCES accounts(id),

    CONSTRAINT chk_entry_amount
        CHECK (amount > 0)
);


CREATE INDEX idx_ledger_transactions_from_account
    ON ledger_transactions(from_account_id);

CREATE INDEX idx_ledger_transactions_to_account
    ON ledger_transactions(to_account_id);

CREATE INDEX idx_ledger_entries_transaction
    ON ledger_entries(transaction_id);

CREATE INDEX idx_ledger_entries_account
    ON ledger_entries(account_id);