CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE urls
(
    url       VARCHAR(4096) PRIMARY KEY NOT NULL,
    hash        VARCHAR(6) NOT NULL,
    created_at timestamptz DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE hashes
(
    hash VARCHAR(6) PRIMARY KEY
);