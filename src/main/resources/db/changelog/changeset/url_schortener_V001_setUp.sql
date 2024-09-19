CREATE SEQUENCE unique_number_seq
START WITH 1
INCREMENT BY 1;

CREATE TABLE url(
    id BIGINT PRIMARY KEY ,
    hash VARCHAR(6) UNIQUE,
    url VARCHAR(128) NOT NULL UNIQUE,
    created_at TIMESTAMP default current_timestamp
);

CREATE TABLE hash(
    id BIGINT PRIMARY KEY,
    hash VARCHAR(6) NOT NULL UNIQUE
);