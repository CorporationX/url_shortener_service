CREATE TABLE urls (
    hash VARCHAR(6) NOT NULL PRIMARY KEY,
    url VARCHAR NOT NULL,
    created_at TIMESTAMP DEFAULT current_timestamp
);

CREATE TABLE hashes (
    hash VARCHAR(6) NOT NULL PRIMARY KEY
);

CREATE SEQUENCE unique_numbers_seq
    START WITH 1
    INCREMENT BY 1;