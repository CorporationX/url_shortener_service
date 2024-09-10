CREATE TABLE url (
    id         bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    hash       varchar(6) UNIQUE NOT NULL,
    url        text UNIQUE       NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE hash (
    id   bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    hash varchar(6) UNIQUE NOT NULL
);

CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1;