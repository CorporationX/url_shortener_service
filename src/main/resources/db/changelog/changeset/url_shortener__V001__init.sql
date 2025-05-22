CREATE TABLE url (
    hash varchar(8) PRIMARY KEY UNIQUE NOT NULL,
    url varchar(512) NOT NULL,
    last_get_at timestamptz NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE INDEX idx_hash ON url(hash);
CREATE INDEX idx_url ON url(url);

CREATE SEQUENCE free_hash_id_seq
    START WITH 1
    INCREMENT By 1000;

CREATE TABLE free_hash (
    id BIGINT PRIMARY KEY DEFAULT nextval('free_hash_id_seq'),
    hash VARCHAR(7) UNIQUE NOT NULL
);

CREATE SEQUENCE unique_hash_number_seq
    START WITH 250000
    INCREMENT BY 7;