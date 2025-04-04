CREATE TABLE url
(
    hash       VARCHAR(6) COLLATE "C" PRIMARY KEY,
    url        VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    delete_at  TIMESTAMP    NOT NULL
);

ALTER TABLE url
    ADD CONSTRAINT hash_length_check CHECK ( length(hash) >= 5 AND length(hash) <= 6);

CREATE TABLE hash
(
    hash VARCHAR(6) COLLATE "C" PRIMARY KEY
);

ALTER TABLE hash
    ADD CONSTRAINT hash_length_check CHECK ( length(hash) >= 5 AND length(hash) <= 6);

CREATE SEQUENCE unique_hash_number_seq
    START WITH 14776336
    INCREMENT BY 1
    MAXVALUE 916132831
    CYCLE;