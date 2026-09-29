ALTER TABLE ledger_transactions
ADD COLUMN idempotency_key VARCHAR(100);

ALTER TABLE ledger_transactions
ADD CONSTRAINT uq_ledger_transaction_idempotency_key
UNIQUE (idempotency_key);