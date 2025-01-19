CREATE TABLE hash (
    hash VARCHAR(8) PRIMARY KEY NOT NULL UNIQUE
);

CREATE TABLE url (
    hash VARCHAR(8) PRIMARY KEY NOT NULL UNIQUE,
    url VARCHAR(1024) NOT NULL UNIQUE,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE SEQUENCE unique_number_sequence
    START WITH 1
    INCREMENT BY 1;