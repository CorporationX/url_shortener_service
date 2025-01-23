CREATE TABLE url_hash
(
    hash VARCHAR(6) PRIMARY KEY
);

CREATE TABLE url
(
    hash       VARCHAR(6) PRIMARY KEY,
    url        VARCHAR     NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE SEQUENCE unique_number_sequence
    START WITH 1
    INCREMENT 1;