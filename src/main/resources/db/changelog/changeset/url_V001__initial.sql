CREATE
    sequence IF NOT EXISTS unique_number_sequence
    START
        WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS url
(
    hash       VARCHAR(6)                  NOT NULL,
    url        VARCHAR(2048)               NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_url PRIMARY KEY (hash)
);

CREATE TABLE IF NOT EXISTS hash
(
    hash VARCHAR(6) NOT NULL,
    CONSTRAINT pk_hash PRIMARY KEY (hash)
);