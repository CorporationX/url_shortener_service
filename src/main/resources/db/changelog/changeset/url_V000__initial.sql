CREATE SEQUENCE hash_id_seq
AS BIGINT
INCREMENT BY 1
START WITH 1;

CREATE TABLE hash (
    hash varchar(6) PRIMARY KEY
);

CREATE TABLE url (
    hash varchar(6) PRIMARY KEY,
    url VARCHAR NOT NULL,
    created_at timestamptz DEFAULT current_timestamp NOT NULL
);
