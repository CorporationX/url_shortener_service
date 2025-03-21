CREATE TABLE IF NOT EXISTS url (
    hash varchar(6) PRIMARY KEY,
    url varchar,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS hash (
    hash varchar PRIMARY KEY
);

CREATE SEQUENCE IF NOT EXISTS unique_number_seq START WITH 1 INCREMENT BY 1;