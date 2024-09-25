CREATE UNIQUE INDEX IF NOT EXISTS idx_url_hash ON url USING hash (hash);
CREATE UNIQUE INDEX IF NOT EXISTS idx_url_hash ON hash USING hash (hash);
