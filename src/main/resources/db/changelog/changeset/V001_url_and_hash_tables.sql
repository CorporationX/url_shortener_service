CREATE TABLE IF NOT EXISTS url (
                                   hash VARCHAR(6) PRIMARY KEY,
                                   url VARCHAR(1024) NOT NULL,
                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS hash (
    hash VARCHAR(6) PRIMARY KEY NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS unique_number_seq START 1;