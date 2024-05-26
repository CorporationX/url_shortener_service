CREATE TABLE IF NOT EXISTS url (
    hash varchar(6) PRIMARY KEY NOT NULL,
    url varchar NOT NULL,
    created_at TIMESTAMPTZ DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS hash (
    hash varchar(6) PRIMARY KEY NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS unique_number_seq
START WITH 1
INCREMENT BY 1;