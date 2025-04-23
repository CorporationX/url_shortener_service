CREATE TABLE IF NOT EXISTS global_counter (
    id INT PRIMARY KEY,
    value BIGINT
);

INSERT INTO global_counter (id, value)
VALUES (1, 0)
ON CONFLICT (id) DO NOTHING;

CREATE TABLE IF NOT EXISTS short_url (
    hash VARCHAR(8) PRIMARY KEY,
    original_url VARCHAR(1024) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

