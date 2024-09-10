CREATE SEQUENCE unique_hash_number_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE url (
    hash VARCHAR(6) PRIMARY KEY UNIQUE,
    url VARCHAR(128) NOT NULL UNIQUE,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE hash (
    hash VARCHAR(6) PRIMARY KEY UNIQUE
);