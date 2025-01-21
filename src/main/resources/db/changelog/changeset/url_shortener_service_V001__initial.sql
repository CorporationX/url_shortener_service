CREATE SEQUENCE unique_hash_number_seq START WITH 238328 INCREMENT BY 83;

CREATE TABLE hash (
    hash VARCHAR(8) PRIMARY KEY
);

CREATE TABLE url (
    hash VARCHAR(8) PRIMARY KEY,
    url VARCHAR(2048) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    expires_at TIMESTAMPTZ DEFAULT NOW() + INTERVAL '6 months'
);

CREATE INDEX expires_at_index ON url (expires_at);