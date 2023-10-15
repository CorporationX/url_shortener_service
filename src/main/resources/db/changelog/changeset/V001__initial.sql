CREATE TABLE IF NOT EXISTS url (
    hash varchar(6) PRIMARY KEY NOT NULL,
    url text,
    created_at timestamptz
);

CREATE TABLE IF NOT EXISTS hash (
    id bigint GENERATED ALWAYS AS IDENTITY UNIQUE,
    hash varchar(256) PRIMARY KEY NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS unique_number_seq
    START WITH 1
    INCREMENT BY 1;