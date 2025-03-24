CREATE TABLE url_mappings
(
    hash       CHAR(5) PRIMARY KEY,
    long_url   TEXT        NOT NULL,
    created_at TIMESTAMP   NOT NULL,
    expired_at TIMESTAMP,
    status     VARCHAR(20) NOT NULL
);

CREATE INDEX idx_expired_at ON url_mappings (expired_at);

CREATE TABLE free_hashes
(
    hash       CHAR(5) PRIMARY KEY,
    created_at TIMESTAMP NOT NULL
);

CREATE SEQUENCE hash_sequence
    START WITH 14776336
    INCREMENT BY 1
    CACHE 10000;
