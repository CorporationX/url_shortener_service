CREATE TABLE hash
(
    hash varchar(6) PRIMARY KEY
        CHECK (length(hash) > 0 AND length(hash) <= 6)
);

CREATE TABLE url
(
    hash varchar(6) PRIMARY KEY,
    url  varchar UNIQUE NOT NULL
        CHECK (url ~ '^https?://.*' OR url ~ '^[a-zA-Z][a-zA-Z0-9+.-]*://.*'),
    created_at timestamptz NOT NULL
);

CREATE SEQUENCE unique_number_seq START WITH 1 INCREMENT BY 1;