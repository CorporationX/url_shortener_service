CREATE TABLE url (
    hash varchar(6) PRIMARY KEY,
    url varchar(8000) UNIQUE,
    created_at timestamp
);

CREATE TABLE hash (
    hash varchar(6) PRIMARY KEY
);

CREATE SEQUENCE unique_number_seq
    START 1;