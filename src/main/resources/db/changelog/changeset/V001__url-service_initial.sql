CREATE TABLE url
(
    hash VARCHAR(6) PRIMARY KEY ,
    url VARCHAR NOT NULL ,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE hash
(
    hash VARCHAR(6) PRIMARY KEY
);

CREATE SEQUENCE unique_number_seq;

CREATE UNIQUE INDEX url_index ON url(url);