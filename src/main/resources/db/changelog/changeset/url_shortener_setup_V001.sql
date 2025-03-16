CREATE TABLE url (
    hash VARCHAR(8) PRIMARY KEY,
    url VARCHAR(16384) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp,
    expired_at timestamptz
);

CREATE UNIQUE INDEX hash_idx ON url (hash);
CREATE UNIQUE INDEX url_idx ON url (url);

CREATE TABLE hash (
    hash VARCHAR(8) PRIMARY KEY
);

CREATE SEQUENCE unique_numbers_seq
    INCREMENT 1
    MINVALUE 1000000000
    CACHE 1
    NO CYCLE;