CREATE TABLE IF NOT EXISTS url (
    hash VARCHAR(8) UNIQUE NOT NULL PRIMARY KEY,
    url VARCHAR(512) NOT NULL,
    last_get_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ DEFAULT current_timestamp
);

CREATE INDEX IF NOT EXISTS idx_url_hash ON url(hash);
CREATE INDEX IF NOT EXISTS idx_url_url ON url(url);

CREATE SEQUENCE IF NOT EXISTS unique_hash_number_seq
    START WITH 250000
    INCREMENT BY 7;

CREATE TABLE IF NOT EXISTS hash (
    id BIGINT PRIMARY KEY DEFAULT nextval('unique_hash_number_seq'),
    hash VARCHAR(7) UNIQUE NOT NULL
);