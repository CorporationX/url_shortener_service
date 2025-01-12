CREATE TABLE IF NOT EXISTS url
(
    hash       VARCHAR(6) PRIMARY KEY,
    long_url   VARCHAR(2048) NOT NULL UNIQUE,
    created_at timestamptz DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS hash
(
    hash VARCHAR(6) PRIMARY KEY
);

CREATE SEQUENCE IF NOT EXISTS unique_numbers_for_hashes
    START WITH 1
    INCREMENT BY 1;
