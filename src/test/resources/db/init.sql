CREATE SEQUENCE IF NOT EXISTS unique_hash_number_seq
    START WITH 11
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS url
(
    id         BIGSERIAL PRIMARY KEY,
    hash       VARCHAR(6) UNIQUE,
    url        VARCHAR(128) NOT NULL,
    created_at timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS hash
(
    id   BIGSERIAL PRIMARY KEY,
    hash VARCHAR(6)
);

INSERT INTO hash (hash)
VALUES
    ('d'),
    ('e'),
    ('f'),
    ('g'),
    ('h'),
    ('i'),
    ('j');

INSERT INTO url (hash, url, created_at)
VALUES
    ('a', 'http://url_1.ru', '2024-10-17 12:00:00'),
    ('b', 'http://url_2.ru', '2024-10-16 12:00:00'),
    ('c', 'http://url_3.ru', '2024-10-15 12:00:00');
