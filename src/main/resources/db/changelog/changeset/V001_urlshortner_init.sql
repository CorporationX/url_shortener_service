CREATE TABLE url
(
    hash           VARCHAR(6) PRIMARY KEY,
    url            VARCHAR(255) NOT NULL,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
);

CREATE TABLE hash
(
    hash           VARCHAR(6) PRIMARY KEY
);

CREATE SEQUENCE unique_hash_number_seq
    START WITH 1000000000
    INCREMENT BY 1;