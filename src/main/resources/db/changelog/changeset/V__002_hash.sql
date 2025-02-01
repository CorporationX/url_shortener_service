CREATE TABLE hash
(
    hash VARCHAR(6) PRIMARY KEY
);

ALTER TABLE url
    ADD CONSTRAINT url_unique_url UNIQUE (url);
ALTER TABLE hash
    ADD CONSTRAINT hash_unique_hash UNIQUE (hash);