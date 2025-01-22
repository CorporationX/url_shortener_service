CREATE TABLE IF NOT EXISTS url (
    hash        VARCHAR(6) PRIMARY KEY,
    url         VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS hash (
    hash        VARCHAR(6) PRIMARY KEY
);

CREATE SEQUENCE IF NOT EXISTS unique_hash_number_seq START WITH 1 INCREMENT BY 1;