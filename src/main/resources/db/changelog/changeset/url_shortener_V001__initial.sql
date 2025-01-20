CREATE TABLE url (
    hash            VARCHAR(6) PRIMARY KEY,
    url             VARCHAR(2000) NOT NULL,
    status          VARCHAR(20) NOT NULL,
    expiration_time TIMESTAMPTZ  NOT NULL
);

CREATE TABLE hash (
    hash VARCHAR(6) PRIMARY KEY
);

CREATE SEQUENCE unique_number_seq START WITH 1000000000 INCREMENT BY 1;