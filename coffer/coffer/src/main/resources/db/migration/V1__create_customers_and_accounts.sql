


CREATE TABLE customers (
    id UUID PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE accounts(
    id UUID PRIMARY KEY,
    account_number VARCHAR(12) NOT NULL UNIQUE,
    type VARCHAR(8) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(6) NOT NULL,
    customer_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_accounts_customer
        FOREIGN KEY (customer_id)
        REFERENCES customers(id)
);