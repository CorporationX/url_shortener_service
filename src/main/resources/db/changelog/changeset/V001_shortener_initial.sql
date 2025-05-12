CREATE TABLE url
(
    hash       VARCHAR(6),
    url        VARCHAR(2048) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (hash)
);

CREATE TABLE hash
(
    hash VARCHAR(6) PRIMARY KEY
);

CREATE SEQUENCE unique_number_seq
    START 1;
