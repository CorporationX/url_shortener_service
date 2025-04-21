CREATE TABLE url (
    url PRIMARY KEY VARCHAR NOT NULL,
    hash VARCHAR(7) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp NOT NULL
);

CREATE TABLE hash (
     id VARCHAR(7) PRIMARY KEY
);

CREATE SEQUENCE hash_sequence AS BIGINT INCREMENT BY 1 START WITH 1;