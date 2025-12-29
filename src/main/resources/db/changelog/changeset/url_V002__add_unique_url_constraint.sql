
CREATE UNIQUE INDEX idx_url_url_unique ON url(url);

ALTER TABLE hash ADD COLUMN available BOOLEAN;

ALTER TABLE hash ALTER COLUMN available SET DEFAULT true;

UPDATE hash SET available = true WHERE available IS NULL;

ALTER TABLE hash ALTER COLUMN available SET NOT NULL;

CREATE INDEX idx_hash_available ON hash(available) WHERE available = true;

