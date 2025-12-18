-- liquibase formatted sql

-- changeset maksimmalarov:1765478131913-1
CREATE SEQUENCE IF NOT EXISTS unique_hash_number_seq
    START WITH 2232832
    INCREMENT BY 1;

-- changeset maksimmalarov:1765478131913-2
CREATE TABLE hash
(
    id   BIGSERIAL PRIMARY KEY,
    hash VARCHAR(255) NOT NULL UNIQUE
);

-- changeset maksimmalarov:1765478131913-3
CREATE TABLE url
(
    id         BIGSERIAL PRIMARY KEY,
    hash       VARCHAR(255) NOT NULL UNIQUE,
    url        VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE
);

-- changeset maksimmalarov:1765478131913-4
CREATE INDEX idx_hash_hash ON hash(hash);

-- changeset maksimmalarov:1765478131913-5
CREATE INDEX idx_url_hash ON url(hash);
