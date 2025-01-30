CREATE TABLE url (
    hash VARCHAR(16) PRIMARY KEY,
    url VARCHAR(128) NOT NULL,
    created_at timestamptz DEFAULT current_timestamp
);

CREATE TABLE hash (
    hash_value VARCHAR(16) PRIMARY KEY
);

CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1;

CREATE INDEX idx_url_hash ON url (hash);
CREATE INDEX idx_url_createdAt ON url (created_at);