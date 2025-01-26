CREATE SEQUENCE unique_hash_number_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE hash (
    hash varchar(6) PRIMARY KEY
);

CREATE TABLE url (
    hash varchar(6) PRIMARY KEY,
    url varchar(1024) NOT NULL,
    created_at timestamp DEFAULT current_timestamp
);