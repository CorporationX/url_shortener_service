CREATE TABLE url (
 hash varchar(6) PRIMARY KEY NOT NULL,
 url varchar(1024) NOT NULL,
 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE hash(
    hash varchar(6) PRIMARY KEY NOT NULL
);

CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1;
