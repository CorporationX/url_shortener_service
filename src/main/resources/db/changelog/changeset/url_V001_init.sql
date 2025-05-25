CREATE TABLE url
(
    hash       varchar(6)    NOT NULL PRIMARY KEY,
    url        varchar(4096) NOT NULL,
    created_at timestamptz   NOT NULL,
    deleted_at timestamptz   NOT NULL
);

CREATE TABLE hash
(
    hash varchar(6) NOT NULL PRIMARY KEY
);

CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT 1
    MAXVALUE 999999
    CYCLE;