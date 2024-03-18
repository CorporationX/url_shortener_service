CREATE TABLE IF NOT EXISTS url
(
    hash       varchar(6) PRIMARY KEY NOT NULL,
    url        varchar                NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE IF NOT EXISTS hash
(
    hash varchar(6) PRIMARY KEY NOT NULL
);

CREATE SEQUENCE seq_name
    START WITH 1
    INCREMENT BY 1;