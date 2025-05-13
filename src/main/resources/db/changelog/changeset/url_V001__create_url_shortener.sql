CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE CACHE 20;

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

CREATE INDEX idx_url_created_at ON url (created_at);
