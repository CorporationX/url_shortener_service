CREATE TABLE IF NOT EXISTS hash (
                      hash VARCHAR(6) PRIMARY KEY
);
ALTER TABLE hash DROP CONSTRAINT IF EXISTS chk_hash_length;
ALTER TABLE hash ADD CONSTRAINT chk_hash_length CHECK (LENGTH(hash) <= 6);
