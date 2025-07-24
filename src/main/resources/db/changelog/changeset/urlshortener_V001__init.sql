CREATE SEQUENCE unique_number_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS url
(
    id         bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY UNIQUE,
    hash       varchar(7)  NOT NULL UNIQUE,
    url        varchar(2000) NOT NULL UNIQUE,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE INDEX url_hash_idx ON url (hash);
CREATE INDEX url_url_idx ON url (url);

CREATE TABLE IF NOT EXISTS free_hash_pool
(
    hash varchar(7) PRIMARY KEY
);