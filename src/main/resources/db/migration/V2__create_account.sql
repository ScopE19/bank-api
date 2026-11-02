CREATE table account (
    id  UUID    PRIMARY KEY,
    owner_id    UUID    NOT NULL REFERENCES app_user(id),
    account_number VARCHAR(20)  not null UNIQUE,
    balance     NUMERIC(19, 2) not null DEFAULT 0,
    version BIGINT   NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ     NOT NULL,
    CONSTRAINT chk_account_balance_non_negative CHECK ( balance >= 0 )
);

CREATE INDEX idx_account_owner ON account (owner_id);