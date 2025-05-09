CREATE TABLE IF NOT EXISTS global_counter
(
    id    INT PRIMARY KEY,
    value BIGINT
);

INSERT INTO global_counter (id, value)
VALUES (1, 1)
ON CONFLICT (id) DO NOTHING;

CREATE TABLE IF NOT EXISTS url_table
(
    hash         VARCHAR(8) PRIMARY KEY,
    original_url VARCHAR(1024) NOT NULL,
    created_at   TIMESTAMP     NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_hash_url_table_original_url ON url_table (original_url);

