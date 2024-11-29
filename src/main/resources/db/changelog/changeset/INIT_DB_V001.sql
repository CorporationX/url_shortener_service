CREATE TABLE hash (
    hash VARCHAR(6) PRIMARY KEY
);

CREATE SEQUENCE short_link_hash_seq
    START with 1
    INCREMENT BY 1;

ALTER SEQUENCE short_link_hash_seq
    OWNED BY hash.hash;

CREATE TABLE IF NOT EXISTS url (
    hash VARCHAR(6) PRIMARY KEY,
    url VARCHAR(255) NOT NULL,
    created_at timestamptz DEFAULT CURRENT_TIMESTAMP
);