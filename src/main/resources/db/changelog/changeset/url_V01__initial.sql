CREATE TABLE hash
(
    id SERIAL PRIMARY KEY,
    hash VARCHAR(7) NOT NULL UNIQUE
);

CREATE TABLE url
(
    id SERIAL PRIMARY KEY,
    hash VARCHAR(7) NOT NULL UNIQUE,
    url VARCHAR NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE SEQUENCE unique_number_seq
    START WITH 2
    INCREMENT BY 1;