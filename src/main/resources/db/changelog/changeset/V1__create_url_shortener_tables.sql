DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_class WHERE relkind = 'S' AND relname = 'unique_number_seq'
    ) THEN
        CREATE SEQUENCE unique_number_seq START 1 INCREMENT 1;
    END IF;
END
$$;

CREATE TABLE IF NOT EXISTS hash (
    hash VARCHAR(6) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS url (
    hash VARCHAR(6) PRIMARY KEY,
    url TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL
);

ALTER TABLE url
ADD CONSTRAINT uc_url_url UNIQUE (url);