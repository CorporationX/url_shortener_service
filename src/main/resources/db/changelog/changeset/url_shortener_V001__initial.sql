CREATE TABLE hash (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    hash varchar(6) UNIQUE NOT NULL
);

CREATE TABLE url (
    id bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    hash varchar(6) UNIQUE NOT NULL,
    url varchar(2048) NOT NULL,
    created_at timestamp NOT NULL,

    CONSTRAINT fk_hash FOREIGN KEY (hash) REFERENCES hash (hash)
);

CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1;