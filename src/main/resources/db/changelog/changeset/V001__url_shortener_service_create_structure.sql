CREATE TABLE IF NOT EXISTS url (
    hash varchar(6) PRIMARY KEY NOT NULL,
    url varchar NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS hash (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    hash varchar(6) NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS unique_number_seq