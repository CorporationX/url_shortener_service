CREATE TABLE url
(
    id         BIGSERIAL PRIMARY KEY,
    long_url   VARCHAR(2048) NOT NULL UNIQUE,
    hash       VARCHAR(6) NOT NULL UNIQUE,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE hash
(
    id          BIGSERIAL PRIMARY KEY,
    hash_string VARCHAR(6) NOT NULL UNIQUE
);

CREATE SEQUENCE unique_hash_number_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE;

INSERT INTO url (long_url, hash, created_at)
VALUES
    ('https://example.com', '9WH8As', CURRENT_TIMESTAMP - INTERVAL '11 days'),
    ('https://www.lipsum.com', 'Uy30G1', CURRENT_TIMESTAMP);

INSERT INTO hash (hash_string)
VALUES
    ('T45uI9'),
    ('C6Kl3s');

