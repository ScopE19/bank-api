CREATE TABLE app_user (
    id  UUID    PRIMARY KEY,
    email   VARCHAR(254) NOT NULL UNIQUE,
    password_hash   VARCHAR(100)    NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    created_at TIMESTAMPTZ  not null
)