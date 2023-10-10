CREATE TABLE url (
    hash varchar(6) PRIMARY KEY,
    url text,
    created_at timestamptz
);

CREATE TABLE hash (
    hash varchar(256) PRIMARY KEY
);

CREATE SEQUENCE unique_numbers_seq;
