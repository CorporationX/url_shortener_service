CREATE TABLE url (
    hash      VARCHAR(7) PRIMARY KEY,
    url       TEXT      NOT NULL,
    create_at TIMESTAMP NOT NULL
);

CREATE TABLE hash (
    hash VARCHAR(7) PRIMARY KEY
);

CREATE SEQUENCE unique_hash_number_seq
    START WITH 1
    INCREMENT BY 1;