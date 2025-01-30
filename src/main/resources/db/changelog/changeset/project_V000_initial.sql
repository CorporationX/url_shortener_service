CREATE TABLE IF NOT EXISTS url (
    hash        VARCHAR(6) PRIMARY KEY,
    url         TEXT NOT NULL CHECK (LENGTH(url) > 0),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_hash_length_url CHECK (LENGTH(hash) = 6)
    );

CREATE TABLE IF NOT EXISTS hash (
    id          BIGINT PRIMARY KEY DEFAULT nextval('unique_number_seq'),
    hash        VARCHAR(6) NOT NULL UNIQUE,
    CONSTRAINT check_hash_length_hash CHECK (LENGTH(hash) = 6)
    );

CREATE SEQUENCE IF NOT EXISTS unique_number_seq
    START WITH 916132832
    INCREMENT BY 1;