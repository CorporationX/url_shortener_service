CREATE TABLE url (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    hash char(7) UNIQUE NOT NULL,
    url varchar(2048) UNIQUE NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE hash (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    hash char(7) UNIQUE NOT NULL
);

CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1;