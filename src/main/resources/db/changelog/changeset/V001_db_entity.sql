CREATE TABLE url(
    hash varchar(6) PRIMARY KEY,
    url varchar(128) NOT NULL,
    created_at timestamp default CURRENT_DATE
);

CREATE TABLE hash(
    hash varchar(6) PRIMARY KEY
);

CREATE SEQUENCE unique_number_seq
INCREMENT 1
START 1;