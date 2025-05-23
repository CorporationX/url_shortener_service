CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE hash
(
    hash_value VARCHAR(6) PRIMARY KEY
);

CREATE TABLE url_mapping
(
    hash_value   VARCHAR(6) PRIMARY KEY,
    original_url VARCHAR(2048) NOT NULL,
    created_at   TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_url_mapping_short_hash ON url_mapping (hash_value);
CREATE INDEX idx_url_mapping_created_at ON url_mapping(created_at);