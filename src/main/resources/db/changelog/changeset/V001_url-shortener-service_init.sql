/*
 Hash
*/

CREATE TABLE IF NOT EXISTS hash
(
    hash VARCHAR(6) NOT NULL,
    CONSTRAINT pk_hash PRIMARY KEY (hash)
);

/*
 URL
*/

CREATE TABLE IF NOT EXISTS url
(
    hash       VARCHAR(6)                NOT NULL,
    url        VARCHAR(64)                 NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_url PRIMARY KEY (hash)
);

/*
 Unique Numbers
*/

CREATE SEQUENCE IF NOT EXISTS unique_number_seq
    INCREMENT 1
    START 1;