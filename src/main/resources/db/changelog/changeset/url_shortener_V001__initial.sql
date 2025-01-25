CREATE SEQUENCE IF NOT EXISTS unique_hash_number_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS url (
    hash varchar(7) PRIMARY KEY,
    url varchar(1024) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS hash (
    hash varchar(7) PRIMARY KEY
);