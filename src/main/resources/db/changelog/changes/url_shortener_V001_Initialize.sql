CREATE TABLE IF NOT EXISTS url (
    hash varchar(6) NOT NULL PRIMARY KEY,
    url varchar(2048) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS hash (
    hash varchar(6) NOT NULL PRIMARY KEY
);

CREATE SEQUENCE IF NOT EXISTS unique_number_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9999999999999999999999999999
    CACHE 1;