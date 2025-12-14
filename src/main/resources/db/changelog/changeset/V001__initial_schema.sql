-- Create sequence for generating unique numbers
CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1;

-- Create hash table for storing all generated hashes
CREATE TABLE hash
(
    hash VARCHAR(6) PRIMARY KEY NOT NULL
);

-- Create url table for storing hash-URL associations
CREATE TABLE url
(
    hash       VARCHAR(6)       PRIMARY KEY NOT NULL,
    url        VARCHAR(2048)    NOT NULL,
    created_at TIMESTAMP        NOT NULL DEFAULT NOW()
);