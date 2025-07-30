--liquibase formatted sql

--changeset kfrolov:create_unique_number_seq_20250729
CREATE SEQUENCE unique_number_seq
    START WITH 16777216
    INCREMENT BY 1
    NO MAXVALUE;

--changeset kfrolov:create_free_hash_storage_table_20250729
CREATE TABLE free_hash_storage (
    hash VARCHAR(6) PRIMARY KEY,

    CHECK (char_length(hash) IN (5, 6))
);

--changeset kfrolov:create_url_table_20250729
CREATE TABLE short_url (
    hash VARCHAR(6) PRIMARY KEY,
    actual_url TEXT NOT NULL,
    expiration_time TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CHECK (char_length(hash) IN (5, 6)),
    CHECK (expiration_time > created_at)
);