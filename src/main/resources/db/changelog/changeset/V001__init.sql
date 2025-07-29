CREATE TABLE IF NOT EXISTS shedlock (
    name VARCHAR(64) PRIMARY KEY,
    locked_until TIMESTAMP WITH TIME ZONE,
    locked_at TIMESTAMP WITH TIME ZONE,
    locked_by VARCHAR(255)
    );

CREATE SEQUENCE IF NOT EXISTS unique_hash_seq
    AS BIGINT
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS hashes (
    hash VARCHAR(6) PRIMARY KEY);

CREATE TABLE IF NOT EXISTS urls (
    hash VARCHAR(6) PRIMARY KEY,
    url TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT current_timestamp
    );

CREATE INDEX idx_url_hash ON urls(hash);