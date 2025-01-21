CREATE TABLE url (
    hash CHAR(6) PRIMARY KEY,
    url TEXT NOT NULL,
    created_at timestamptz DEFAULT current_timestamp NOT NULL
);

CREATE TABLE hash (
    hash CHAR(6) PRIMARY KEY
);

CREATE SEQUENCE unique_number_seq
START WITH 1
INCREMENT BY 1