CREATE TABLE IF NOT EXISTS url (
                                   hash varchar(6) PRIMARY KEY,
                                   url varchar(2048) NOT NULL,
                                   created_at timestamp DEFAULT current_timestamp NOT NULL
);

CREATE TABLE IF NOT EXISTS hash (
                                    hash varchar(6) PRIMARY KEY
);

CREATE SEQUENCE IF NOT EXISTS unique_number_seq
    INCREMENT BY 1
    START WITH 1;