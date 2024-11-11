ALTER TABLE url
    ADD COLUMN created_at timestamp DEFAULT current_timestamp;

CREATE TABLE hash (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    hash varchar(128) NOT NULL
);