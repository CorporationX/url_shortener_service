CREATE TABLE IF NOT EXISTS url
(
    hash             VARCHAR(6) PRIMARY KEY,
    url              VARCHAR(512) NOT NULL,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_received_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS hash
(
    hash varchar(6) PRIMARY KEY
);

CREATE SEQUENCE IF NOT EXISTS unique_number_seq start 1 increment 1;