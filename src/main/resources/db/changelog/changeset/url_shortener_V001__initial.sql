CREATE TABLE url (
    hash            VARCHAR(6) PRIMARY KEY,
    url             TEXT       NOT NULL,
    expiration_time TIMESTAMP  NOT NULL
);

CREATE TABLE hash (
    hash VARCHAR(6) PRIMARY KEY
);

CREATE SEQUENCE unique_number_seq
    START WITH 1000000000
    INCREMENT BY 1;