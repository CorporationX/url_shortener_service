CREATE SEQUENCE unique_hash_number_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE hash
(
    id   bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    hash varchar(7) NOT NULL UNIQUE
);

CREATE TABLE url
(
    id           bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    original_url varchar(255) NOT NULL,
    hash         varchar(7)   NOT NULL UNIQUE,
    created_at   timestamptz DEFAULT current_timestamp,
    updated_at   timestamptz DEFAULT current_timestamp
)
