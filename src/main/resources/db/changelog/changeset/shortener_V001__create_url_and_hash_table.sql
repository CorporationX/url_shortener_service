CREATE TABLE urls
(
    hash       VARCHAR(6) PRIMARY KEY,
    url        VARCHAR(255) NOT NULL,
    expired_at TIMESTAMP    NOT NULL,
    created_at TIMESTAMP    NOT NULL
);

CREATE TABLE hashes
(
    hash VARCHAR(6) PRIMARY KEY
);

CREATE SEQUENCE unique_number_seq
    AS BIGINT
    INCREMENT BY 1
    START WITH 1;