CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE hash (
    hash VARCHAR(6) PRIMARY KEY
);

CREATE TABLE url (
    hash VARCHAR(6) PRIMARY KEY,
    url TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_url_url ON url(url);