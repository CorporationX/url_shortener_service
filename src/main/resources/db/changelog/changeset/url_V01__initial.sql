CREATE TABLE hash
(
    hash VARCHAR(7) NOT NULL UNIQUE
);

CREATE TABLE url
(
    hash VARCHAR(7) NOT NULL UNIQUE,
    url VARCHAR NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    CONSTRAINT fk_hash FOREIGN KEY (hash) REFERENCES hash (hash)
);

CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1;