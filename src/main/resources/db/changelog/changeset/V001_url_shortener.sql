CREATE TABLE urls (
    hash       varchar(6) PRIMARY KEY,
    url        varchar(2500) NOT NULL,
    created_at timestamp     NOT NULL
);

CREATE TABLE generated_urls (
    hash varchar(6) PRIMARY KEY
);

CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1;