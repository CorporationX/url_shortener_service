CREATE TABLE IF NOT EXISTS url (
    hash VARCHAR(6) CONSTRAINT url_hash_pk PRIMARY KEY,
    url TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS hash (
    hash VARCHAR(6) CONSTRAINT hash_hash_pk PRIMARY KEY
);

CREATE SEQUENCE IF NOT EXISTS unique_number_seq;