CREATE SEQUENCE unique_hash_number_sequence
    INCREMENT 1
    START WITH 1;

CREATE TABLE hashes (
    hash VARCHAR(6) NOT NULL,
    PRIMARY KEY (hash)
);

CREATE TABLE url (
    hash VARCHAR(6) NOT NULL,
    created_at TIMESTAMP,
    PRIMARY KEY (hash)
);