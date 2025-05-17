CREATE SEQUENCE unique_hash_number_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE hash
(
    hash VARCHAR(6) NOT NULL,
    CONSTRAINT hash_pk PRIMARY KEY (hash)
);

CREATE TABLE url
(
    hash       VARCHAR(6) NOT NULL,
    url        TEXT       NOT NULL,
    created_at TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT url_pk PRIMARY KEY (hash)
);

CREATE INDEX url_created_at_idx ON url(created_at);