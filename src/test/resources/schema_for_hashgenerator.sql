CREATE SEQUENCE unique_number_seq
    START WITH 20000
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS hash
(
    hash VARCHAR(6) PRIMARY KEY NOT NULL UNIQUE
);

INSERT into hash
VALUES ('hash1'),
       ('hash2'),
       ('hash3'),
       ('hash4'),
       ('hash5'),
       ('hash6'),
       ('hash7'),
       ('hash8'),
       ('hash9'),
       ('hash10'),
       ('hash11'),
       ('hash12'),
       ('hash13'),
       ('hash14'),
       ('hash15'),
       ('hash16'),
       ('hash17'),
       ('hash18'),
       ('hash19');

CREATE TABLE IF NOT EXISTS url
(
    hash       VARCHAR(6) PRIMARY KEY NOT NULL,
    url        VARCHAR(255)           NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_hash_id FOREIGN KEY (hash) REFERENCES hash (hash)
);