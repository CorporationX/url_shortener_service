--liquibase formatted sql

--changeset sanya_popenko:url
--comment: Создание таблицы url
CREATE TABLE url (
    hash      VARCHAR(6) PRIMARY KEY,
    url       VARCHAR(4000) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset sanya_popenko:hash
--comment: Создание таблицы hash
CREATE TABLE hash (
    hash VARCHAR(6) PRIMARY KEY
);

--changeset sanya_popenko:unique_number_seq
--comment: Создание секвенции unique_number_seq
CREATE SEQUENCE unique_number_seq
-- TODO: пересчитать
    START WITH 56800235584
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;