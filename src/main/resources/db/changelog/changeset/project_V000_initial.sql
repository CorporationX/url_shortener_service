CREATE TABLE IF NOT EXISTS url (
    id          BIGINT PRIMARY KEY,
    hash        VARCHAR(6) NOT NULL,
    url         TEXT NOT NULL CHECK (LENGTH(url) > 0),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_hash_length_url CHECK (LENGTH(hash) = 6),
    CONSTRAINT unique_hash_url UNIQUE (hash)
    );

CREATE TABLE IF NOT EXISTS hash (
    id          BIGINT PRIMARY KEY,
    hash        VARCHAR(6) NOT NULL,
    CONSTRAINT check_hash_length_hash CHECK (LENGTH(hash) = 6),
    CONSTRAINT fk_hash_id FOREIGN KEY (id) REFERENCES url(id) ON DELETE CASCADE
    );

CREATE SEQUENCE IF NOT EXISTS unique_number_seq
    START WITH 1
    INCREMENT BY 1;