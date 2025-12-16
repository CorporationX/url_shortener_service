CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1;

CREATE SEQUENCE hash_id_seq
    START WITH 1
    INCREMENT BY 50;

CREATE TABLE hash (
    id bigint PRIMARY KEY,
    hash VARCHAR(7) NOT NULL
);

CREATE TABLE url (
    hash VARCHAR(7) PRIMARY KEY NOT NULL,
    original_url VARCHAR(255) NOT NULL,
    created_at timestamp DEFAULT current_timestamp
);
