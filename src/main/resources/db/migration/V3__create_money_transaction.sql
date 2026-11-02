create table money_transaction (
    id      UUID    PRIMARY KEY,
    type    varchar(20)     not null,
    source_account_id   UUID    references account(id),
    target_account_id      UUID    references account(id),
    amount      numeric(19, 2)  NOT NULL,
    initiated_by    UUID    references app_user (id),
    created_at      TIMESTAMPTZ     not null,
    CONSTRAINT chk_txn_amount_positive CHECK ( amount > 0 ),
    CONSTRAINT chk_txn_type check ( type in ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER'))
);

CREATE INDEX idx_txn_source ON money_transaction (source_account_id, created_at desc);
CREATE INDEX idx_txn_target ON money_transaction (target_account_id, created_at desc);