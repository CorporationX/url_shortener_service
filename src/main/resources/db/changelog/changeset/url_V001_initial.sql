CREATE TABLE url
(
    hash       VARCHAR(8) COLLATE "C" PRIMARY KEY,
    url        VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    delete_at  TIMESTAMP    NOT NULL
);

CREATE TABLE hash
(
    hash VARCHAR(8) COLLATE "C" PRIMARY KEY
) WITH (
      fillfactor = 90
      );

CREATE SEQUENCE unique_hash_number_seq
    START WITH 14776336
    INCREMENT BY 1
    MAXVALUE 916132831
    CYCLE;