CREATE TABLE url
(
    id         UUID PRIMARY KEY,
    hash       VARCHAR(6) UNIQUE NOT NULL,
    url        TEXT UNIQUE       NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE hash
(
    id   UUID PRIMARY KEY,
    hash VARCHAR(6) UNIQUE NOT NULL
);

CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1;