CREATE SEQUENCE IF NOT EXISTS unique_hash_seq
    AS BIGINT
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS hashes (
    hash VARCHAR(6) PRIMARY KEY not null unique);

CREATE TABLE IF NOT EXISTS urls (
    hash VARCHAR(6) PRIMARY KEY not null unique,
    url TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT current_timestamp,
    CONSTRAINT fk_hash FOREIGN KEY(hash) REFERENCES hashes(hash)
    );

CREATE INDEX idx_url_hash ON urls(hash);