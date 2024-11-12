CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS hash (
    hash    VARCHAR(6) PRIMARY KEY NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS url (
    hash        VARCHAR(6) PRIMARY KEY NOT NULL,
    url         VARCHAR(255) NOT NULL,
    created_at  timestamptz DEFAULT current_timestamp,

    CONSTRAINT  fk_hash_id FOREIGN KEY (hash) REFERENCES hash (hash)
);