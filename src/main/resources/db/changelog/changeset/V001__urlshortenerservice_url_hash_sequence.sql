CREATE SEQUENCE if not exists unique_number_seq
    START WITH 1
    INCREMENT BY 1 CACHE 1;

CREATE TABLE IF NOT EXISTS url
(
    hash       VARCHAR(6) PRIMARY KEY,
    url        TEXT      NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expired_at TIMESTAMP NOT NULL
);

CREATE TABLE if not exists hash
(
    hash VARCHAR(6) PRIMARY KEY
);