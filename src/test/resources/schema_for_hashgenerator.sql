CREATE SEQUENCE unique_number_seq
    START WITH 20000
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS hash
(
    hash VARCHAR(6) PRIMARY KEY NOT NULL UNIQUE
);

INSERT into hash
VALUES ('123qwe'),
       ('sfdg45'),
       ('56fgf4'),
       ('67ghn7'),
       ('kjh46f'),
       ('kde889'),
       ('90dd4f'),
       ('09k890'),
       ('0kf4hg'),
       ('09fkj7');

CREATE TABLE IF NOT EXISTS url
(
    hash       VARCHAR(6) PRIMARY KEY NOT NULL,
    url        VARCHAR(255)           NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,

    CONSTRAINT fk_hash_id FOREIGN KEY (hash) REFERENCES hash (hash)
);