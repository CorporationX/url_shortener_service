CREATE TABLE url
(
    hash       varchar(6) PRIMARY KEY,
    url        varchar(2000),
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE hash
(
    hash varchar(6) PRIMARY KEY
);

CREATE SEQUENCE unique_number_seq START WITH 1000000000 INCREMENT BY 1;