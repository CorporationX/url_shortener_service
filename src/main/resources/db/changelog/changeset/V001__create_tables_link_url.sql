CREATE TABLE IF NOT EXISTS url (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    hash varchar(7) NOT NULL UNIQUE,
    url text NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS hash (
    hash varchar(7) PRIMARY KEY
);

CREATE SEQUENCE IF NOT EXISTS unique_number_seq START WITH 1 INCREMENT BY 1;
