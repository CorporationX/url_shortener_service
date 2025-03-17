CREATE TABLE IF NOT EXISTS hash
(
    hash            VARCHAR(6) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS url
(
    hash            VARCHAR(6) PRIMARY KEY,
    url             VARCHAR NOT NULL,
    created_at      TIMESTAMP NOT NULL
);

CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT 1;
