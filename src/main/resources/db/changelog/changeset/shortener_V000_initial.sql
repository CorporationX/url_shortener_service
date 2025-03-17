CREATE TABLE IF NOT EXISTS hash
(
    hash VARCHAR(6) PRIMARY KEY,

    CONSTRAINT hash_length CHECK (LENGTH(hash) = 6)
);

CREATE TABLE IF NOT EXISTS url
(
    id         BIGSERIAL PRIMARY KEY,
    hash       VARCHAR(6) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_hash FOREIGN KEY (hash) REFERENCES hash (hash),
    CONSTRAINT url_hash_length CHECK (LENGTH(hash) = 6)
);

CREATE SEQUENCE unique_hash_number_seq
    START WITH 1
    INCREMENT BY 1;

