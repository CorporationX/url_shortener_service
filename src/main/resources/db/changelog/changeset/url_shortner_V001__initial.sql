CREATE TABLE IF NOT EXISTS urls (
    hash varchar(7) PRIMARY KEY NOT NULL,
    url varchar(512) UNIQUE NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS hash (
    hash varchar(7) PRIMARY KEY NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS unique_number_seq;
