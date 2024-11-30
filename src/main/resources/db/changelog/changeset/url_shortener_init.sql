CREATE TABLE url
(
    id         UUID PRIMARY KEY,
    hash       VARCHAR(6) NOT NULL UNIQUE,
    url        TEXT       NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE hash
(
    id   UUID PRIMARY KEY,
    hash VARCHAR(6) NOT NULL UNIQUE
);

CREATE SEQUENCE unique_number_seq
    START WITH 1000000000
    INCREMENT BY 1;