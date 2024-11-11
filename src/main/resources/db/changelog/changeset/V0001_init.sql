CREATE SEQUENCE IF NOT EXISTS unique_number_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS urls
(
    hash       VARCHAR(6) PRIMARY KEY ,
    url        varchar(2048) NOT NULL,
    created_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_hash FOREIGN KEY (hash) REFERENCES hashes(hash)
);

CREATE TABLE IF NOT EXISTS hashes
(
    hash varchar(6) PRIMARY KEY
)
