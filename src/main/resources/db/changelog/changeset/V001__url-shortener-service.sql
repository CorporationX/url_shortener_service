CREATE SEQUENCE unique_number_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE url (
    hash VARCHAR(20) PRIMARY KEY not null,
    url VARCHAR(255) NOT NULL UNIQUE,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE hash (
    hash VARCHAR(20) PRIMARY KEY NOT NULL
);
