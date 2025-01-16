CREATE SEQUENCE unique_number_seq
START WITH 1
INCREMENT BY 1;

CREATE TABLE url (
    hash varchar(6) PRIMARY KEY,
    url varchar(1024) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE hash (
    hash varchar(6) PRIMARY KEY
);