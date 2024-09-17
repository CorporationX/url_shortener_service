CREATE SEQUENCE unique_hash_number_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE url
(
    id         BIGSERIAL PRIMARY KEY,
    hash       VARCHAR(6) UNIQUE,
    url        VARCHAR(128) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE hash
(
    id   BIGSERIAL PRIMARY KEY,
    hash VARCHAR(6) NOT NULL UNIQUE
);