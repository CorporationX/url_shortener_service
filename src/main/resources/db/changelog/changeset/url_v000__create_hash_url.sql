CREATE SEQUENCE IF NOT EXISTS unique_hash_number_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS  hash
(
    hash VARCHAR(6) NOT NULL,
    CONSTRAINT hash_pk PRIMARY KEY (hash)
);

CREATE TABLE IF NOT EXISTS url
(
    hash       VARCHAR(6) NOT NULL,
    url        VARCHAR(2048)       NOT NULL,
    created_at TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT url_pk PRIMARY KEY (hash)
);

CREATE INDEX IF NOT EXISTS  url_created_at_idx ON url(created_at);
CREATE INDEX IF NOT EXISTS  url_url_idx ON url USING hash (url);
