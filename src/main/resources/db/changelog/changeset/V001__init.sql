CREATE SEQUENCE unique_hash_seq
    START WITH 1
    INCREMENT BY 1;

    CREATE TABLE hashes (
    hash VARCHAR(6) PRIMARY KEY)

    CREATE TABLE urls (
    hash VARCHAR(6) PRIMARY KEY
    url TEXT NOT NULL
    created_at TIMESTAMP NOT NULL
    CONSTRAINT fk_hash FOREIGN KEY(hash) REFERENCES hashes(hash)
    )

    CREATE INDEX idx_url_hash ON urls(hash);