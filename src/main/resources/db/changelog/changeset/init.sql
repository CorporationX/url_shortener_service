CREATE TABLE url (
                         hash VARCHAR(7) PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
                         url VARCHAR(255) NOT NULL UNIQUE,
                         created_at timestamptz DEFAULT current_timestamp,
);

CREATE TABLE hash (
                     hash VARCHAR(7) PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
);

CREATE SEQUENCE unique_hash_number_seq
    START WITH 123
    INCREMENT BY 1;