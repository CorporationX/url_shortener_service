CREATE TABLE url (
    hash VARCHAR(6) PRIMARY KEY, 
    url TEXT, 
    created_at timestamptz DEFAULT current_timestamp);

CREATE TABLE hash (
    hash VARCHAR(6) PRIMARY KEY);

CREATE SEQUENCE unique_number_seq 
    INCREMENT BY 1 
    MINVALUE 1 
    MAXVALUE 99999999999 
    START WITH 1;