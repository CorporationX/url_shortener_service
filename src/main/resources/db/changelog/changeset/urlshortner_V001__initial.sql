CREATE TABLE IF NOT EXISTS url(
    hash       varchar(6) PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    url        varchar(256),
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS hash (
    hash varchar(6) PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE
);

CREATE SEQUENCE IF NOT EXISTS unique_number_seq;