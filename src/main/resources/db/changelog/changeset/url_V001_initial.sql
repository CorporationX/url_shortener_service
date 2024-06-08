CREATE TABLE urls (
    hash VARCHAR(6) PRIMARY KEY,
    url VARCHAR (256) NOT NULL,
    created_at TIMESTAMPTZ DEFAULT current_timestamp
);

CREATE TABLE hashes (
    hash VARCHAR(6) PRIMARY KEY
);

CREATE SEQUENCE unique_numbers_seq
    START WITH 1
    INCREMENT BY 1;