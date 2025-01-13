CREATE TABLE IF NOT EXISTS url
(
    hash       VARCHAR(6) PRIMARY KEY,
    url        VARCHAR NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS hash
(
    hash VARCHAR(6) PRIMARY KEY
);

CREATE SEQUENCE IF NOT EXISTS unique_number_seq START 1;