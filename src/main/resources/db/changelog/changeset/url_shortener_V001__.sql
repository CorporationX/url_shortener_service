CREATE TABLE url (
    hash       VARCHAR(6)    PRIMARY KEY,
    url        VARCHAR(2000) NOT NULL,
    created_at TIMESTAMP     NOT NULL DEFAULT current_timestamp,
    expires_at TIMESTAMP     NOT NULL
    );