CREATE TABLE IF NOT EXISTS url (
    hash varchar(6) NOT NULL PRIMARY KEY,
    url varchar(2048) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS hash (
    hash varchar(6) NOT NULL PRIMARY KEY
);

CREATE SEQUENCE IF NOT EXISTS unique_number_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;