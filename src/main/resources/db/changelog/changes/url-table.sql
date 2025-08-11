CREATE TABLE IF NOT EXISTS url (
    hash VARCHAR(50) PRIMARY KEY,
    original_url VARCHAR(2048) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_url_original_url ON url(original_url);
