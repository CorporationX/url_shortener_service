CREATE TABLE url_hashes
(
    hash VARCHAR(6) PRIMARY KEY
);

CREATE TABLE urls
(
    hash       VARCHAR(6) PRIMARY KEY,
    url        VARCHAR     NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE SEQUENCE unique_number_sequence
    START WITH 1
    INCREMENT 1;