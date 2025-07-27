--liquibase formatted sql

--changeset nnovopashin:create_unique_sequence
CREATE SEQUENCE unique_number_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE;

--changeset nnovopashin:create_hash_table
CREATE TABLE hash (
    hash    VARCHAR(6) PRIMARY KEY,

    CHECK (length(hash) = 6)
)

--changeset nnovopashin:create_url_table
CREATE TABLE url (
    hash        VARCHAR(6) PRIMARY KEY,
    url         VARCHAR(256),
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at  TIMESTAMP NOT NULL,

    CHECK (length(hash) = 6)
)