CREATE TABLE url (
    hash varchar(6) PRIMARY KEY NOT NULL,
    url varchar(4096) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE hash (
    hash varchar(64) PRIMARY KEY NOT NULL
);

CREATE SEQUENCE unique_number_seq
START WITH 1
INCREMENT BY 1;