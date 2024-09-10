CREATE TABLE url
(
    hash       varchar(6)   NOT NULL PRIMARY KEY,
    url        varchar(2048) NOT NULL UNIQUE,
    created_at timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE hash
(
    hash varchar(6) NOT NULL PRIMARY KEY
);

CREATE SEQUENCE unique_number_seq START WITH 1 INCREMENT BY 1;