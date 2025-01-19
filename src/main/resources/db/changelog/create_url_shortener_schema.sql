-- create_url_shortener_schema.sql

CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE hash (
    hash VARCHAR(6) NOT NULL,
    CONSTRAINT pk_hash PRIMARY KEY (hash)
);

CREATE TABLE url (
    hash VARCHAR(6) NOT NULL,
    url TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_url PRIMARY KEY (hash),
    CONSTRAINT fk_url_hash FOREIGN KEY (hash) REFERENCES hash (hash)
);
