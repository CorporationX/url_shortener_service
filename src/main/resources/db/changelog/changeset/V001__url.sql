CREATE TABLE url (
    hash varchar(20) PRIMARY KEY CHECK (LENGTH(hash) <= 6) NOT NULL,
    url varchar(256),
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE hash (
    hash varchar(256) PRIMARY KEY
);

CREATE TABLE sequence (
    unique_number_seq bigint DEFAULT 1
);