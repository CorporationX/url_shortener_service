CREATE TABLE IF NOT EXISTS hash (
    hash       VARCHAR(6) NOT NULL,
    CONSTRAINT pk_hash PRIMARY KEY (hash)
);

CREATE TABLE IF NOT EXISTS url
(
    hash       VARCHAR(6) NOT NULL,
    url        VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_url PRIMARY KEY (hash)
);

CREATE SEQUENCE unique_number_seq
    START 1
    INCREMENT BY 1;