CREATE SEQUENCE IF NOT EXISTS unique_hash_number_seq
    START WITH 250000
    INCREMENT BY 7;

CREATE TABLE IF NOT EXISTS hash (
    id BIGINT PRIMARY KEY DEFAULT nextval('unique_hash_number_seq'),
    hash VARCHAR(7) UNIQUE NOT NULL
);

DROP TABLE IF EXISTS free_hash;
DROP SEQUENCE IF EXISTS free_hash_id_seq;