CREATE TABLE IF NOT EXISTS urls (
    hash VARCHAR(7) PRIMARY KEY,
    original_url TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE SEQUENCE IF NOT EXISTS hashes_id_seq START 1;

CREATE TABLE IF NOT EXISTS hashes (
    id BIGINT PRIMARY KEY DEFAULT nextval('hashes_id_seq'),
    hash VARCHAR(7) UNIQUE,
    used BOOLEAN NOT NULL DEFAULT FALSE
);

INSERT INTO hashes (id, used)
SELECT nextval('hashes_id_seq'), FALSE
FROM generate_series(1, 1000);