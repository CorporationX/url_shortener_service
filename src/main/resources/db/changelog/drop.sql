DROP SEQUENCE unique_hash_number_seq;
DROP TABLE hash;
DROP TABLE url;

DELETE FROM databasechangelog WHERE filename = 'db/changelog/changeset/url_shortener_V001__initial.sql';