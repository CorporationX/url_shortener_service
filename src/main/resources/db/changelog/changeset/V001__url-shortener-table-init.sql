CREATE SEQUENCE unique_number_seq
START WITH 916132832
INCREMENT BY 1
NO MAXVALUE;

CREATE TABLE hash (
    hash            VARCHAR(6) PRIMARY KEY
);

CREATE TABLE url (
    hash            VARCHAR(6) PRIMARY KEY,
    url             VARCHAR(256) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT current_timestamp,
    expiration_time TIMESTAMP NOT NULL
);