CREATE TABLE url (
    hash char(6) PRIMARY KEY,
    url varchar(2048) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE hash (
    hash char(6) PRIMARY KEY
);

CREATE SEQUENCE unique_number_seq
    INCREMENT BY 1
    START WITH 916132832
    CACHE 100;