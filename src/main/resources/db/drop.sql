DROP TABLE url;
DROP TABLE hash;
DROP SEQUENCE unique_number_seq;
DELETE FROM databasechangelog
     WHERE filename = 'db/changelog/changeset/url_V001_initial.sql';