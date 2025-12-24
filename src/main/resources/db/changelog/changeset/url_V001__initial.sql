CREATE TABLE urls (
    hash varchar(6) PRIMARY KEY NOT NULL UNIQUE,
    url varchar(2048) NOT NULL,
    created_at timestamptz DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE hashes (
    hash varchar(6) PRIMARY KEY NOT NULL UNIQUE
);

CREATE SEQUENCE unique_number_seq
    AS bigint
    INCREMENT BY 1
    START WITH 1;