CREATE SEQUENCE IF NOT EXISTS unique_number_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE hashes (
    hash VARCHAR(6) PRIMARY KEY
);

CREATE TABLE urls (
    hash        VARCHAR(6)  PRIMARY KEY,
    url         TEXT        NOT NULL,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_urls_hashes
    FOREIGN KEY (hash) REFERENCES hashes(hash)
);
