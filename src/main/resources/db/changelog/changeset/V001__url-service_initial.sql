CREATE TABLE url
(
    hash CHAR(6) PRIMARY KEY ,
    url VARCHAR,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE hash
(
    hash CHAR(6) PRIMARY KEY
);

CREATE SEQUENCE unique_number_seq;

CREATE INDEX url_index ON url(url);