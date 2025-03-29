CREATE TABLE IF NOT EXISTS hash (
    hash varchar(6) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS url (
    hash varchar(6) PRIMARY KEY,
    url text NOT NULL,
    created_at timestamp DEFAULT current_timestamp
);

CREATE SEQUENCE IF NOT EXISTS unique_number_seq
    INCREMENT BY 1
    START WITH 1;