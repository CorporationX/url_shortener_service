CREATE TABLE urls
(
    hash       VARCHAR(6) PRIMARY KEY NOT NULL UNIQUE,
    url        VARCHAR(255)           NOT NULL UNIQUE,
    created_at TIMESTAMP              NOT NULL
);

CREATE TABLE hashes
(
    hash VARCHAR(6) PRIMARY KEY NOT NULL UNIQUE
);

CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1000;
