CREATE TABLE url(
    hash varchar(6) PRIMARY KEY,
    url varchar (2048) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE hash(
    hash varchar(6) PRIMARY KEY
);

CREATE SEQUENCE id_seq START 1 INCREMENT BY 1;