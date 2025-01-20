CREATE TABLE IF NOT EXISTS url
(
    hash       VARCHAR(6) PRIMARY KEY,
    url        TEXT      NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE UNIQUE INDEX idx_hash ON url (hash);

CREATE TABLE IF NOT EXISTS hash
(
    hash VARCHAR PRIMARY KEY UNIQUE
);
CREATE UNIQUE INDEX idx_hash_on_hash ON hash (hash);

CREATE SEQUENCE unique_number_seq START WITH 1 INCREMENT BY 1;