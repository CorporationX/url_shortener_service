CREATE UNIQUE INDEX IF NOT EXISTS url_index ON url (url);

ALTER TABLE url
    ADD CONSTRAINT url_unique_constraint UNIQUE (url);
