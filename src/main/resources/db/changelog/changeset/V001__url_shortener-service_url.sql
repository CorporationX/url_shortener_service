CREATE TABLE url (
    hash       VARCHAR(7) PRIMARY KEY,
    url        text NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE hash (
     hash VARCHAR(6) UNIQUE NOT NULL
);

CREATE SEQUENCE unique_number_seq START 1;
