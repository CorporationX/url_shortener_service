CREATE TABLE IF NOT EXISTS url
(
    hash       varchar(6) PRIMARY KEY,
    url        varchar,
    created_at timestamp
);

CREATE TABLE IF NOT EXISTS hash
(
    hash varchar(6) PRIMARY KEY
);

CREATE SEQUENCE IF NOT EXISTS unique_number_seq