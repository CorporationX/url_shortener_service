--liquibase formatted sql

--changeset sanya_popenko:url
--comment: Создание таблицы url с id, генерируемым из url_id_seq
CREATE TABLE url (
    hash      VARCHAR(7) PRIMARY KEY,
    url       VARCHAR(4000) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

--changeset sanya_popenko:hash
--comment: Создание таблицы hash с id, генерируемым из hash_id_seq
CREATE TABLE hash (
    hash VARCHAR(7) PRIMARY KEY
);

--changeset sanya_popenko:unique_number_seq
--comment: Создание секвенции unique_number_seq
CREATE SEQUENCE unique_number_seq
    START WITH 56800235584
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;