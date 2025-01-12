CREATE TABLE url (
    hash VARCHAR(6) PRIMARY KEY,
    url VARCHAR(2000) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
    );

CREATE TABLE hash(
    hash VARCHAR(6) PRIMARY KEY
    );

CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
