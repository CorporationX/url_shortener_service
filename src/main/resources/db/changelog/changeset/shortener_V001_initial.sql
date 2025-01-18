CREATE SCHEMA IF NOT EXISTS shortener_schema;

CREATE TABLE shortener_schema.url
(
    hash       VARCHAR(6) PRIMARY KEY,
    url        TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE shortener_schema.hash
(
    hash VARCHAR(6) PRIMARY KEY
);

CREATE TABLE shortener_schema.hash_generation_lock
(
    id        SERIAL PRIMARY KEY,
    locked    BOOLEAN NOT NULL,
    locked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO shortener_schema.hash_generation_lock (locked)
VALUES (false);

CREATE SEQUENCE shortener_schema.unique_number_seq
    START WITH 1
    INCREMENT BY 1;