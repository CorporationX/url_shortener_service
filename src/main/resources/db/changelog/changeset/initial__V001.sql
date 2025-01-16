CREATE SEQUENCE url_seq
    START WITH 1
    INCREMENT BY 100;

CREATE SEQUENCE hash_seq
    START WITH 1
    INCREMENT BY 100;

CREATE TABLE url
(
    id         BIGINT PRIMARY KEY DEFAULT nextval('url_seq'),
    hash       VARCHAR(6) NOT NULL UNIQUE,
    url        TEXT       NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    expired_at TIMESTAMPTZ
);

CREATE TABLE hash
(
    id   BIGINT PRIMARY KEY DEFAULT nextval('hash_seq'),
    hash VARCHAR(6) NOT NULL UNIQUE
);

CREATE SEQUENCE url_hash_seq
    INCREMENT BY 1
    START WITH 1;

CREATE INDEX idx_expired_at
    ON url (expired_at) WHERE expired_at IS NOT NULL;