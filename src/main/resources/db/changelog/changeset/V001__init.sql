--liquibase formatted sql

--changeset kfrolov:create_unique_number_seq_20250716
CREATE SEQUENCE unique_number_seq
    START WITH 916132832
    INCREMENT BY 1
    NO MAXVALUE;

--changeset kfrolov:create_free_hash_storage_table_20250716
CREATE TABLE free_hash_storage (
    hash VARCHAR(7) PRIMARY KEY
);

--changeset kfrolov:create_url_table_20250716
CREATE TABLE short_url (
    hash VARCHAR(7) PRIMARY KEY,
    actual_url TEXT NOT NULL,
    expiration_time TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);