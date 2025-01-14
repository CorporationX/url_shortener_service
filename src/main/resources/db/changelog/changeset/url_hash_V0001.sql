CREATE SCHEMA IF NOT EXISTS url_shortener_schema;

CREATE TABLE url_shortener_schema.url
(
    hash       VARCHAR(6) PRIMARY KEY,
    url        TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE url_shortener_schema.hash
(
    hash VARCHAR(6) PRIMARY KEY
);

CREATE SEQUENCE url_shortener_schema.unique_number_seq
    START WITH 1
    INCREMENT BY 1;
