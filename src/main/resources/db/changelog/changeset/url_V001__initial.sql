CREATE TABLE url (
    hash VARCHAR(6) PRIMARY KEY UNIQUE NOT NULL,
    url VARCHAR(256) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE hash (
    hash VARCHAR(6) PRIMARY KEY UNIQUE NOT NULL
);

CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1;
