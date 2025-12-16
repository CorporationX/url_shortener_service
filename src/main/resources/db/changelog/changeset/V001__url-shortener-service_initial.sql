CREATE TABLE url (
                    hash VARCHAR(6) PRIMARY KEY NOT NULL,
                    url VARCHAR(4096) NOT NULL,
                    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE hash (
                    hash VARCHAR(6) PRIMARY KEY NOT NULL
);

CREATE SEQUENCE unique_number_seq
    INCREMENT BY 1
    MINVALUE 1
    START WITH 1;


