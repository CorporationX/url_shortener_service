CREATE TABLE url (
    hash       CHAR(6)          PRIMARY KEY,
    url        VARCHAR(4096)    NOT NULL,
    created_at TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP

    CONSTRAINT fk_url_hash FOREIGN KEY (hash) REFERENCES hash(hash)
);

CREATE TABLE hash (
    hash CHAR(6) PRIMARY KEY
);

CREATE SEQUENCE unique_number_seq
    START 1
    INCREMENT 1
