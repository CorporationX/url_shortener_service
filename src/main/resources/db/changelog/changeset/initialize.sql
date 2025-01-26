CREATE TABLE urls (
                      hash VARCHAR(7) PRIMARY KEY NOT NULL UNIQUE,
                      url VARCHAR(255) NOT NULL UNIQUE,
                      created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE hash (
                           hash VARCHAR(7) PRIMARY KEY NOT NULL UNIQUE
);

CREATE SEQUENCE unique_hash_number_seq
    START WITH 920000000
    INCREMENT BY 1;