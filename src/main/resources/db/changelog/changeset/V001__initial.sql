CREATE TABLE url
(
    hash       VARCHAR(6)   NOT NULL PRIMARY KEY,
    url        VARCHAR NOT NULL,
    created_at timestamptz DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE hash
(
    hash VARCHAR(6) NOT NULL PRIMARY KEY
);

CREATE sequence unique_number_sequence
    start 1
    increment 1;