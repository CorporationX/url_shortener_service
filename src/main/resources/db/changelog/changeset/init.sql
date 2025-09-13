--liquibase formatted sql

--changeset you:001-bootstrap
--comment: bootstrap changelog so Liquibase has something to parse
-- This is a harmless statement for most DBs:
SELECT 1;