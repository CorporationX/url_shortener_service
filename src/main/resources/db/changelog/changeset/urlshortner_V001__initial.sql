CREATE TABLE IF NOT EXISTS url(
    hash       varchar(6) PRIMARY KEY,
    url        varchar(256) NOT NULL UNIQUE,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS hash (
    hash varchar(6) PRIMARY KEY
);

CREATE SEQUENCE IF NOT EXISTS unique_number_seq AS INT START 1;