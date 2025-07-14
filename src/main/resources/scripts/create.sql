-- changeset <author>:<id>
-- formatted sql

CREATE TABLE url (
    hash        VARCHAR(6)    PRIMARY KEY NOT NULL,
    url         VARCHAR(2048) NOT NULL,
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
);

CREATE TABLE hash (
    hash VARCHAR(6) PRIMARY KEY NOT NULL,
);

CREATE SEQUENCE unique_number_seq(
    START WITH 1
    INCREMENT BY 1
);