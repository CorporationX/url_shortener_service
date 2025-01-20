CREATE TABLE IF NOT EXISTS url (
                     hash VARCHAR(6) PRIMARY KEY,
                     url TEXT NOT NULL,
                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS unique_number_seq
    START WITH 1
    INCREMENT BY 1
    NO CYCLE;
ALTER TABLE url DROP CONSTRAINT IF EXISTS chk_hash_length;
ALTER TABLE url ADD CONSTRAINT  chk_hash_length CHECK (LENGTH(hash) <= 6);