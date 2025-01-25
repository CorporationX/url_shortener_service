CREATE TABLE IF NOT EXISTS url (
    hash        VARCHAR(6) PRIMARY KEY,
    url         TEXT NOT NULL CHECK (LENGTH(url) > 0),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_hash_length_url CHECK (LENGTH(hash) = 6)
    );

CREATE TABLE IF NOT EXISTS hash (
    hash        VARCHAR(6) PRIMARY KEY,
    CONSTRAINT check_hash_length_hash CHECK (LENGTH(hash) = 6),
    CONSTRAINT fk_hash FOREIGN KEY (hash) REFERENCES url(hash) ON DELETE CASCADE
    );

CREATE SEQUENCE IF NOT EXISTS unique_number_seq
    START WITH 1
    INCREMENT BY 1;