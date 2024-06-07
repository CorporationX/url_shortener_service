CREATE TABLE url (
    hash varchar(6) PRIMARY KEY,
    url varchar NOT NULL,
    created_at timestamp DEFAULT current_timestamp
);

CREATE TABLE hash (
    hash varchar(2048) PRIMARY KEY
);

CREATE SEQUENCE unique_number_seq START 1 INCREMENT BY 1;