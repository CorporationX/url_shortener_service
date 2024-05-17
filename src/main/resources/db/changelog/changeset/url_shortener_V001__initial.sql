CREATE sequence unique_number_sequence
    start 1
    increment 1;

CREATE TABLE url(
    hash       VARCHAR(6)  PRIMARY KEY,
    url        VARCHAR(2048)     NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE hash (
    hash VARCHAR(6) NOT NULL PRIMARY KEY
);