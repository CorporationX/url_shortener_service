CREATE TABLE IF NOT EXISTS url
(
    hash                     VARCHAR(6) PRIMARY KEY,
    url                      VARCHAR(2048) NOT NULL UNIQUE,
    last_ttl_expiration_date DATE      DEFAULT CURRENT_TIMESTAMP,
    created_at               TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS hash
(
    hash VARCHAR(6) PRIMARY KEY
);

CREATE SEQUENCE IF NOT EXISTS hash_unique_number_seq START WITH 1 INCREMENT BY 1;