CREATE TABLE url
(
    hash       VARCHAR(255) PRIMARY KEY,
    url        VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP           NOT NULL,
    delete_at  TIMESTAMP           NOT NULL
);

CREATE TABLE hash
(
    hash VARCHAR PRIMARY KEY
);

CREATE SEQUENCE unique_hash_number_seq
    START WITH 1
    INCREMENT BY 1;