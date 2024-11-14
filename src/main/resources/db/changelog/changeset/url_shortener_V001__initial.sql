CREATE TABLE url (
    hash       VARCHAR(6)          PRIMARY KEY,
    url        TEXT       NOT NULL,
    created_at TIMESTAMP  NOT NULL
);

CREATE TABLE hash (
    hash VARCHAR(6) PRIMARY KEY
);

CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1;