CREATE TABLE url (
    hash VARCHAR(6) PRIMARY KEY,
    url TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT current_timestamp);

CREATE TABLE hash (
    hash VARCHAR(6) PRIMARY KEY);

CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1;

CREATE UNIQUE INDEX url_index ON url(url);



