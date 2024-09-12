CREATE TABLE url
(
    hash       varchar(8) PRIMARY KEY,
    url        varchar(2048) NOT NULL,
    created_at timestamptz   NOT NULL DEFAULT current_timestamp
);

CREATE TABLE hash
(
    hash varchar(8) PRIMARY KEY
);

CREATE SEQUENCE unique_number_seq
    START WITH 4000
    INCREMENT BY 1;